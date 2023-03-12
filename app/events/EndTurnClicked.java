package events;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.GameState;
import structures.basic.Card;
import structures.basic.PlayerAvatar;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.AttackChecker;
import utils.MovementChecker;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// if it is player's turn, switch to AI's turn
	    if (gameState.isPlayerTurn()) {

			gameState.changeTurn();

			// discard any unused mana
	        gameState.setHumanMana(0);
	        BasicCommands.setPlayer1Mana(out, gameState.getPlayer());

			// draw a card
	        if (gameState.playerDrawCard(out)) {
	            BasicCommands.addPlayer1Notification(out, "Draw a card", 2);
	        }
			gameState.displayHand(out);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// AI turn starts
			BasicCommands.addPlayer1Notification(out, "AI's Turn", 2);
			gameState.setAiMana(gameState.getTurnNum() + 1);
			BasicCommands.setPlayer2Mana(out, gameState.getAi());

			// reset units action
			gameState.resetAllAction();

			// AI runs on a sperate thread
	        AI opponent = new AI(out, gameState, message);
	        opponent.start();
	    }
	}


	/*
	 * This is the Thread for running AI's code
	 */
	class AI extends Thread {
	    ActorRef out;
	    GameState gameState;
	    JsonNode message;

	    public AI(ActorRef out, GameState gameState, JsonNode message) {
	        this.out = out;
	        this.gameState = gameState;
	        this.message = message;
	    }

	    public void run() {
		List<Unit> aiUnits = gameState.getAIUnits();// get AI units
		List<Unit> playerUnits =gameState.getPlayerUnits(); // get player units
		List<Card> aiHand = gameState.getAIHand();// AI Hand
		Board board = gameState.getGameBoard(); // get the gameboard

		// locate the player avatar 
		Unit humanAvatar = null;
		for (Unit unit : playerUnits) {
			if (unit.getId() == 99) {
				humanAvatar = unit;
			}
		}

		/*
		 * First component of the AI
		 * Playing Spell cards
		 */

		// check if staff of ykir is in hand
		Card staffOfYKir = null;
		for (Card card :
				aiHand) {
			System.out.println(card.getCardname());
			if (card.getCardname().equals("Staff of Y'Kir'")){
				staffOfYKir = card;
			}
		}

		// staff of ykir is always played first before any unit action to maximize its value
		if (staffOfYKir != null) {
			staffOfYKir.playCard(out, gameState, staffOfYKir.checkTargets(gameState).get(0));
			gameState.removeAICard(staffOfYKir);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// check if entropic decay is in hand
		Card entropicDecay = null;
		for (Card card :
				aiHand) {
			if (card.getCardname().equals("Entropic Decay")){
				entropicDecay = card;
			}
		}

		// play entropic decay if available 
		if (entropicDecay != null) {
			List<Tile> targets = entropicDecay.checkTargets(gameState);

			// To maximize its utility, the player unit with most health is identified as the target
			int maxHealth = 0;
			Tile target = null;
			for (Tile tile :
					targets) {
				Unit unit = tile.getUnit();
				if (unit.getPlayer() == 1 && unit.getHealth() > maxHealth){
					maxHealth = unit.getHealth();
					target = tile;
				}
			}

			// not wasting it on units with too little health
			if (maxHealth > 5) {
				entropicDecay.playCard(out, gameState, target);
				gameState.removeAICard(entropicDecay);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
		/*
		 * Second component of the AI
		 * Playing units on board
		 */

		// loop until all units have spent their actions
		Unit selected = null;
		while (true) {

			// select an available unit
			for (Unit unit : aiUnits) {
				if (unit.canMove() || (unit.canAttack() && !AttackChecker.checkAttackRange(unit.getTile(), board, 2).isEmpty())) {
					selected = unit;
				}
			}

			// break out of the loop if no unit can be selected
			if (selected == null) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}

			// proceed with the next action only when there is no on-going attack or movement in the frontend UI
			if (gameState.isReady() && !gameState.onGoingAttack()) {

				// if the unit still has move action
				if (selected.canMove()) {
					// get the move-and-attack targets
					List<Tile> targetsForAttack = AttackChecker.checkAllAttackRange(MovementChecker.checkMovement(selected.getTile(), board), board, 2);
					targetsForAttack.addAll(AttackChecker.checkAttackRange(selected.getTile(), board, 2));
	
					// always perform an attack if there is any valid target
					if (!targetsForAttack.isEmpty()) {
						Tile targetForAttack = null;
						int maxScore = 0;

						// prioritize enemy unit with the highest score
						for (Tile tile : targetsForAttack) {
							if (attackScore(tile.getUnit(),selected.getAttack()) > maxScore) {
								targetForAttack = tile;
								maxScore = attackScore(tile.getUnit(),selected.getAttack());
							}
						}
						
						// highlight the target
						BasicCommands.drawTile(out, targetForAttack, 2);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						BasicCommands.drawTile(out, targetForAttack, 0);

						//perform the attack
						gameState.attack(selected, targetForAttack.getUnit(), out);
					
					// else the unit has no attackable target and thus can only move
					}else {
						List<Tile> range = MovementChecker.checkMovement(selected.getTile(), board);
						int x = humanAvatar.getTile().getTilex();
						int y = humanAvatar.getTile().getTiley();
	
						//check which tile is the closest to human's avatar
						//try to move the unit as close as possible
						Tile closest = null;
						int shortestDistance = 12; // 12 tile is the maximum distance
						for (Tile tile : range) {
							int distance = Math.abs(tile.getTilex() - x) + Math.abs(tile.getTiley() - y);
							if (distance < shortestDistance) {
								closest = tile;
								shortestDistance = distance;
							}
						}
	
						//move the unit closer to player's avatar
						gameState.moveUnit(selected, closest, out);
	
					}
				
				// else if the unit can only attack
				// this conditional allows double attack unit to exhaust its attack actions
				}else if (selected.canAttack()) {

					// get basic attack targets
					List<Tile> targetsForAttack = AttackChecker.checkAttackRange(selected.getTile(), board, 2);
	
					// initiate an attack if there is any valid target
					if (!targetsForAttack.isEmpty()) {
						Tile targetForAttack = null;
						int maxScore = 0;
						// prioritize enemy unit with the highest score
						for (Tile tile : targetsForAttack) {
							if (attackScore(tile.getUnit(),selected.getAttack()) > maxScore) {
								targetForAttack = tile;
								maxScore = attackScore(tile.getUnit(), selected.getAttack());
							}
						}
						
						// highlight the attack target
						BasicCommands.drawTile(out, targetForAttack, 2);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						BasicCommands.drawTile(out, targetForAttack, 0);
						//perform the attack
						gameState.attack(selected, targetForAttack.getUnit(), out);
					}
	
						
				}
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			selected = null;
		}
			
		
			
			
			/*
			 * Third componenet of AI
			 * Summoning units
			 */
	
			
			
			int xPos = humanAvatar.getTile().getXpos();
			int yPos = humanAvatar.getTile().getYpos();

			// loop through hand until there is no mana to play any card or hand is empty
			while(!gameState.getAIHand().isEmpty() && LowestManaCard(gameState.getAIHand()).getManacost() <= gameState.getAiMana()){
				
				Map<Integer, Integer> map1 = init_AiCard(gameState.getAIHand()); // map for ranking cards

				// get the card with top score
				Card topScoreCard = Play_Card(map1,gameState.getAIHand(),gameState.getAiMana());

				if (topScoreCard == null) {
					break;
				}

				List<Tile> targets = topScoreCard.checkTargets(gameState);

				// put the unit as close as possible to human player's avatar
				Tile summon = getClosestTile(targets, xPos, yPos);

				// if the unit is flying or ranged, put them as far away as possible for safety and room for maneuverability
				if (topScoreCard.getCardname().equals("WindShrike") || topScoreCard.getCardname().equals("Pyromancer")) {
					summon = getSafestTile(targets, xPos, yPos);
				}

				// highlight the target tile
				BasicCommands.drawTile(out, summon, 1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				BasicCommands.drawTile(out, summon, 0);

				//play the card
				topScoreCard.playCard(out, gameState, summon);
				gameState.removeAICard(topScoreCard);
				try {
		            Thread.sleep(2000);
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
			}

			
			/*
			 * End of AI's turn
			 */
			
			// discard unused mana
	        gameState.setAiMana(0);
	        BasicCommands.setPlayer2Mana(out, gameState.getAi());

			// draw a card
			gameState.AIDrawCard(out);

			// player gain mana
			gameState.incrementTurn();
			gameState.setHumanMana(gameState.getTurnNum() + 1);

			// Pass back to Player turn
	        BasicCommands.addPlayer1Notification(out, "Your Turn", 2);
			BasicCommands.setPlayer1Mana(out, gameState.getPlayer());
			gameState.resetAllAction();
			gameState.changeTurn();
	    }
		
	}


	/**
	 *
	 * This method returns a map for ranking the Unit Cards for the AI deck
	 * More powerful units are given a higher score
	 * @param Ai_Cards
	 */
	public Map<Integer, Integer> init_AiCard(List<Card> Ai_Cards){
		Map<Integer,Integer> map=new HashMap<>();
		for(int i=0;i<Ai_Cards.size();i++){
			if(Ai_Cards.get(i).getCardname().equals("Planar Scout")){
				map.put(Ai_Cards.get(i).getId(),30);
			}else if(Ai_Cards.get(i).getCardname().equals("Rock Pulveriser")){
				map.put(Ai_Cards.get(i).getId(),50);
			}else if(Ai_Cards.get(i).getCardname().equals("Pyromancer")){
				map.put(Ai_Cards.get(i).getId(),52);
			}else if(Ai_Cards.get(i).getCardname().equals("Bloodshard Golem")){
				map.put(Ai_Cards.get(i).getId(),54);
			}else if(Ai_Cards.get(i).getCardname().equals("Blaze Hound")){
				map.put(Ai_Cards.get(i).getId(),61);
			}else if(Ai_Cards.get(i).getCardname().equals("WindShrike")){
				map.put(Ai_Cards.get(i).getId(),60);
			}else if(Ai_Cards.get(i).getCardname().equals("Hailstone Golem")){
				map.put(Ai_Cards.get(i).getId(),56);
			}else if(Ai_Cards.get(i).getCardname().equals("Serpenti")){
				map.put(Ai_Cards.get(i).getId(),70);
			}
		}
		return map;
	}





	/* This is a helper method for giving attack priority to an enemy unit.
	 * It takes into account factors such as the unit's health, attack, special abilities, 
	 * whether it is the avatar, whether this is a killing blow etc to compute a score.
	 * This score is used in choosing the target for attack in the AI logic
	 */
	public int attackScore(Unit unit, int attack) {

		int score = 0;

		score += Math.min(25, unit.getAttack() * 5);
		score += Math.min(25, 60 / unit.getHealth());
		if (unit.getId() == 99) {
			score+=40;
		}
		if (unit.isRanged()) {
			score+=20;
		}
		if (unit.isProvoke()) {
			score+=20;
		}
		if (!unit.canCounterAttack()) {
			score+=10;
		}
		if (attack >= unit.getHealth()) {
			score+=30;
		}

		return score;
	}


	// pick the unit card with maximum rating and sufficient mana to play
	public Card Play_Card(Map<Integer,Integer> integerMap,List<Card> cards,int mana){
		Card card=null;
			for(int i=0;i<cards.size();i++){
				if (cards.get(i).getManacost() <= mana && integerMap.containsKey(cards.get(i).getId())) {
					if (card == null) {
						card = cards.get(i);
					}
					if(integerMap.get(card.getId())<integerMap.get(cards.get(i).getId())){
						card=cards.get(i);
					}
				}
			}
		return card;
	}

	// get the card with lowest mana cost
	public Card LowestManaCard(List<Card> cards){
		Card targetCard = null;
		int mana = 9;
		
			for(Card card : cards){
				if(card.getManacost()<= mana) {
					mana = card.getManacost();
					targetCard = card;
				}
			}
		
		return targetCard;
	}


	// helper method to pick the tile closest to a given x,y position from a list of tiles
	 public static Tile getClosestTile(List<Tile> tiles, int x, int y) {
	        if (tiles.isEmpty()) {
	            return null;
	        }
	        // Sort the tiles by their distance from (x, y)
	        Collections.sort(tiles, Comparator.comparingDouble(tile -> tile.distanceFromPosition(x, y)));
	        // Find the minimum distance
	        double minDistance = tiles.get(0).distanceFromPosition(x, y);
	        // Find the indexes of all tiles with minimum distance
	        List<Integer> minIndexes = new ArrayList<>();
	        for (int i = 0; i < tiles.size(); i++) {
	            if (tiles.get(i).distanceFromPosition(x, y) == minDistance) {
	                minIndexes.add(i);
	            } 
	        }
	        // Randomly select one of the tiles with minimum distance
	        Random rand = new Random();
	        int randomIndex = minIndexes.get(rand.nextInt(minIndexes.size()));
	        return tiles.get(randomIndex);
	}

	// helper method to pick the tile furthest away to a given x,y position from a list of tiles
	public Tile getSafestTile(List<Tile> tiles, int x, int y) {
		if (tiles.isEmpty()) {
			return null;
		}
		// Sort the tiles by their distance from (x, y)
		Collections.sort(tiles, Comparator.comparingDouble(tile -> tile.distanceFromPosition(x, y)));
		// Find the maximum distance
		double minDistance = tiles.get(tiles.size() - 1).distanceFromPosition(x, y);
		// Find the indexes of all tiles with maximum distance
		List<Integer> minIndexes = new ArrayList<>();
		for (int i = 0; i < tiles.size(); i++) {
			if (tiles.get(i).distanceFromPosition(x, y) == minDistance) {
				minIndexes.add(i);
			} 
		}
		// Randomly select one of the tiles with maximum distance
		Random rand = new Random();
		int randomIndex = minIndexes.get(rand.nextInt(minIndexes.size()));
		return tiles.get(randomIndex);
	}


}
