package events;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;
import java.util.Comparator;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
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
	        }else{
				BasicCommands.addPlayer1Notification(out, "Your hand is full!", 2);
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
			int xPos = 1;
			int yPos = 2;
	    	// code for AI goes in here!!!!
	        try {
	            Thread.sleep(2000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			
			// code for esting cards in AI deck
			
			Map<Integer, Integer> map1 = init_AiCard(gameState.getAIHand());
			for(Unit unit : gameState.getPlayerUnits()) {
				if(unit.getId()==99) {
					xPos = unit.getTile().getXpos();
					yPos = unit.getTile().getYpos();		
				}
			}
			while(!gameState.getAIHand().isEmpty() && LowestManaCard(gameState.getAIHand()).getManacost() <= gameState.getAiMana()){
				
				Card topScoreCard = Play_Card(map1,gameState.getAIHand(),gameState.getAiMana());
				List<Tile> targets = topScoreCard.checkTargets(gameState, 2);
				topScoreCard.playCard(out, gameState,getClosestTile(targets,xPos,yPos) );
				gameState.removeAICard(topScoreCard);
				try {
		            Thread.sleep(2000);
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
			}
			
			// code for testing cards in AI deck
			

	        // Player defaultMana = new Player(20, 0);
	        // gameState.something = true;
	        // gameState.setHumanStep(0);
			try {
	            Thread.sleep(2000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
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
			}else if(Ai_Cards.get(i).getCardname().equals("WindShrike")){
				map.put(Ai_Cards.get(i).getId(),50);
			}else if(Ai_Cards.get(i).getCardname().equals("Hailstone Golem")){
				map.put(Ai_Cards.get(i).getId(),56);
			}else if(Ai_Cards.get(i).getCardname().equals("Serpenti")){
				map.put(Ai_Cards.get(i).getId(),65);
			}
		}
		return map;
	}
	public Card Play_Card(Map<Integer,Integer> integerMap,List<Card> cards,int mana){//Cards with maximum rating and sufficient mana
		Card card=cards.get(0);
			for(int i=1;i<cards.size();i++){
				if (cards.get(i).getManacost() <= mana) {
					if(integerMap.get(card.getId())<integerMap.get(cards.get(i).getId())){
						card=cards.get(i);
					}
				}
			}
		return card;
	}
	public Card LowestManaCard(List<Card> cards){//The card with the smallest mana
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
	            } else {
	                break;
	            }
	        }
	        // Randomly select one of the tiles with minimum distance
	        Random rand = new Random();
	        int randomIndex = minIndexes.get(rand.nextInt(minIndexes.size()));
	        return tiles.get(randomIndex);
	    }
}
