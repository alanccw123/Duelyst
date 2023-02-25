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
	    if (gameState.humanTurn) {
	        Player defaultMana = new Player(20, 0);
	        gameState.humanTurn = false;
	        gameState.something = false;
	        BasicCommands.addPlayer1Notification(out, "Ai Turn", 2);
	        gameState.setAiStep(0);
	        gameState.setHumanMana(gameState.getHumanMana() + 1);
	        BasicCommands.setPlayer2Mana(out, gameState.ai);
	        BasicCommands.setPlayer1Mana(out, defaultMana);
	        boolean handFull = true;
	        if (handFull) {
	            BasicCommands.addPlayer1Notification(out, "Loss Card", 2);
	        }
	        ai aii = new ai(out, gameState, message);
	        aii.start();
	    }
	}

	class ai extends Thread {
	    ActorRef out;
	    GameState gameState;
	    JsonNode message;

	    public ai(ActorRef out, GameState gameState, JsonNode message) {
	        this.out = out;
	        this.gameState = gameState;
	        this.message = message;
	    }

	    public void run() {
	        try {
	            Thread.sleep(5000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        Player defaultMana = new Player(20, 0);
	        gameState.humanTurn = true;
	        gameState.something = true;
	        gameState.setHumanStep(0);
	        gameState.setAiMana(gameState.getAiMana() + 1);
	        BasicCommands.setPlayer1Mana(out, gameState.player);
	        BasicCommands.setPlayer2Mana(out, defaultMana);
	        BasicCommands.addPlayer1Notification(out, "Your Turn", 2);
	    }
	}
}
