package events;
import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
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
		gameState.clearhighlight(out);
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
	        if (gameState.playerDrawCard()) {
	            BasicCommands.addPlayer1Notification(out, "Draw a card", 2);
	        }else{
				BasicCommands.addPlayer1Notification(out, "Your hand is full!", 2);
			}
			gameState.displayHand(out);

			// AI turn starts
			BasicCommands.addPlayer1Notification(out, "AI's Turn", 2);
			gameState.setAiMana(gameState.getTurnNum() + 1);
			BasicCommands.setPlayer2Mana(out, gameState.getAi());

			// reset units action
			gameState.resetAllAction();

			// AI runs a sperate thread
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
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }


	        // Player defaultMana = new Player(20, 0);
	        // gameState.something = true;
	        // gameState.setHumanStep(0);
			
			// discard unused mana
	        gameState.setAiMana(0);
	        BasicCommands.setPlayer2Mana(out, gameState.getAi());

			// draw a card
			gameState.AIDrawCard();

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
}
