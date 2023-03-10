package events;
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
	        // Player defaultMana = new Player(20, 0);
	        // gameState.humanTurn = false;
	        // gameState.something = false;
	        // gameState.setAiStep(0);
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
			// code for AI goes in here!!!!
		List<Unit> aiUnits = gameState.getAIUnits();// get AI units
		List<Unit> playerUnits =gameState.getPlayerUnits(); // get player units
		List<Card> aiHand = gameState.getAIHand();// AI Hand
		Map<Integer, Integer> map = init_AiCard(aiHand);// map for ranking cards
		Board board = gameState.getGameBoard(); // get the gameboard

		// locate the player avatar 
		Unit humanAvatar = null;
		for (Unit unit : playerUnits) {
			if (unit.getId() == 99) {
				humanAvatar = unit;
			}
		}

		// First, use spellcards

		// check if staff of ykir is in hand
		Card staffOfYKir = null;
		for (Card card :
				aiHand) {
			System.out.println(card.getCardname());
			if (card.getCardname().equals("Staff of Y'Kir'")){
				staffOfYKir = card;
			}
		}

		// play staff of ykir if available 
		if (staffOfYKir != null) {
			staffOfYKir.playCard(out, gameState, staffOfYKir.checkTargets(gameState, 2).get(0));
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
			List<Tile> targets = entropicDecay.checkTargets(gameState,2);

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
		
		// Second, spend unit action

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
				break;
			}

			// proceed with the next action only when there is no on-going attack or movement in the frontend UI
			if (gameState.isReady() && !gameState.onGoingAttack()) {

				// if the unit still has move action
				if (selected.canMove()) {
					// get the move-and-attack targets
					List<Tile> targetsForAttack = AttackChecker.checkAllAttackRange(MovementChecker.checkMovement(selected.getTile(), board), board, 2);
					targetsForAttack.addAll(AttackChecker.checkAttackRange(selected.getTile(), board, 2));
	
					// initiate an attack if there any valid target
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
						
						// for debugging
						BasicCommands.drawTile(out, targetForAttack, 2);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						BasicCommands.drawTile(out, targetForAttack, 0);

						//perform the attack
						gameState.attack(selected, targetForAttack.getUnit(), out);
					
					// else the unit can only move
					}else {
						List<Tile> range = MovementChecker.checkMovement(selected.getTile(), board);
						int x = humanAvatar.getTile().getTilex();
						int y = humanAvatar.getTile().getTiley();
	
						//check which tile is the closest to player's avatar
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
				}else if (selected.canAttack()) {

					// get basic attack targets
					List<Tile> targetsForAttack = AttackChecker.checkAttackRange(selected.getTile(), board, 2);
	
					// initiate an attack if there any valid target
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
						
						// for debugging
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
			
			// Third, summon units
			while (Play_Card(map, aiHand, gameState.getAiMana()) != null) {
				Card to_play = Play_Card(map, aiHand, gameState.getAiMana()); // pick the unit card with the highest rating
				List<Tile> targets = to_play.checkTargets(gameState, 2); // get the tiles to summon the unit on
				int x = humanAvatar.getTile().getTilex();
				int y = humanAvatar.getTile().getTiley();

				//check which tile is the closest to player's avatar
				Tile closest = null;
				int shortestDistance = 12; // 12 tile is the maximum distance
				for (Tile tile : targets) {
					int distance = Math.abs(tile.getTilex() - x) + Math.abs(tile.getTiley() - y);
					if (distance < shortestDistance) {
						closest = tile;
						shortestDistance = distance;
					}
				}

				to_play.playCard(out, gameState, closest);
				gameState.removeAICard(to_play);

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
			
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
	 * This is a map for ranking the Unit Cards for the AI deck
	 * More powerful units are given a higher score
	 * @param Ai_Cards
	 * @return
	 */
	public Map<Integer, Integer> init_AiCard(List<Card> Ai_Cards){
		Map<Integer,Integer> map=new HashMap<>();
		for(int i=0;i<Ai_Cards.size();i++){
			if(Ai_Cards.get(i).getCardname().equals("Planar Scout")){
				map.put(Ai_Cards.get(i).getId(),30);
			}else if(Ai_Cards.get(i).getCardname().equals("Rock Pulveriser")){
				map.put(Ai_Cards.get(i).getId(),54);
			}else if(Ai_Cards.get(i).getCardname().equals("Pyromancer")){
				map.put(Ai_Cards.get(i).getId(),65);
			}else if(Ai_Cards.get(i).getCardname().equals("Bloodshard Golem")){
				map.put(Ai_Cards.get(i).getId(),60);
			}else if(Ai_Cards.get(i).getCardname().equals("Blaze Hound")){
				map.put(Ai_Cards.get(i).getId(),61);
			}else if(Ai_Cards.get(i).getCardname().equals("Windshrike")){
				map.put(Ai_Cards.get(i).getId(),50);
			}else if(Ai_Cards.get(i).getCardname().equals("Hailstone Golem")){
				map.put(Ai_Cards.get(i).getId(),56);
			}else if(Ai_Cards.get(i).getCardname().equals("Serpenti")){
				map.put(Ai_Cards.get(i).getId(),70);
			}else {
				map.put(Ai_Cards.get(i).getId(),0);
			}
		}
		return map;
	}


	/***
	 *
	 * 获取Al评分最大的牌并返回
	 * @param integerMap
	 * @param cards
	 * @return
	 */
	public Card Play_Card(Map<Integer,Integer> integerMap,List<Card> cards, int mana){
		Card card=null;//评分最大的牌
		for(int i=0;i<cards.size();i++){
			if (cards.get(i).getManacost() <= mana) {
				if (card == null) {
					card = cards.get(i);
				}else if(integerMap.get(card.getId())<integerMap.get(cards.get(i).getId())){
					card=cards.get(i);
				}
			}	
		}

		return card;
	}

	/**
	 *
	 * 给玩家牌组添加评分
	 * @param P
	 * @return
	 */
	public Map<Integer, Integer> init_PlayerCard(List<Card> P){
		Map<Integer,Integer> map=new HashMap<>();
		for(int i=0;i<P.size();i++){
			if(P.get(i).getCardname().equals("Comodo Charger")){
				map.put(P.get(i).getId(),30);
			}else if(P.get(i).getCardname().equals("Hailstone Golem")){
				map.put(P.get(i).getId(),70);
			}else if(P.get(i).getCardname().equals("Pureblade Enforcer")){
				map.put(P.get(i).getId(),50);
			}else if(P.get(i).getCardname().equals("Azure Herald")){
				map.put(P.get(i).getId(),41);
			}else if(P.get(i).getCardname().equals("Silverguard Knight")){
				map.put(P.get(i).getId(),85);
			}else if(P.get(i).getCardname().equals("Azurite Lion")){
				map.put(P.get(i).getId(),40);
			}else if(P.get(i).getCardname().equals("Fire Spitter")){
				map.put(P.get(i).getId(),59);
			}else if(P.get(i).getCardname().equals("Ironclif Guardian")){
				map.put(P.get(i).getId(),89);
			}else if(P.get(i).getCardname().equals("Truestrike")){
				map.put(P.get(i).getId(),61);
			}else if(P.get(i).getCardname().equals("Sundrop Elixir")){
				map.put(P.get(i).getId(),91);
			}
		}
		return map;
	}


	/**
	 *
	 * 判断电脑攻击对象
	 * @param map
	 * @param gameState
	 * @return
	 */

	// public Unit Attack_Play(Map<Integer, Integer> map,GameState gameState){
	// 	List<Unit> playerUnits = gameState.getPlayerUnits();//玩家的单位
	// 	Unit unit = null;
	// 	boolean a=false;
	// 	for(int i=0;i<playerUnits.size();i++){
	// 		if(playerUnits.get(i).getId()==99){
	// 			a=playerUnits.get(i).canAttack();//判断化身是否可以被攻击
	// 			unit=playerUnits.get(i);
	// 		}
	// 	}
	// 	if(a){
	// 		return unit;
	// 	}else {
	// 		int a1=0;
	// 		for(int i=0;i<playerUnits.size();i++){
	// 			if(map.get(playerUnits.get(i).getId())>a1){
	// 				a1=map.get(playerUnits.get(i).getId());
	// 				unit=playerUnits.get(i);
	// 			}
	// 		}
	// 	}

	// 	return unit;
	// }

	/* This is a helper method for giving attack priority to an enemy unit.
	 * It takes into account the unit's health, attack, special abilities 
	 * and whether it is the avatar, and compute a score of maximum 100.
	 * This score is used in choosing the target for attack in the AI logic
	 */
	public int attackScore(Unit unit, int attack) {
		// if (unit.getId() == 99) {
		// 	return 100;
		// }
		int score = 0;

		score += Math.min(25, unit.getAttack() * 5);
		score += Math.min(25, 60 / unit.getHealth());
		if (unit.getId() == 99) {
			score+=50;
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
			score+=20;
		}

		return score;
	}
}
