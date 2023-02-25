package events;

import structures.GameState;
import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		int handPosition = message.get("position").asInt();
		if(gameState.getHumanStep() < 14) {
			gameState.addHumanStep(1);
			BasicCommands.addPlayer1Notification(out, "step" + gameState.getHumanStep(), 2);
			
		}
		
	}
}
