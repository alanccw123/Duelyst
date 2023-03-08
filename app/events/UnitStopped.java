package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that a unit instance has stopped moving. 
 * The event reports the unique id of the unit.
 * 
 * { 
 *   messageType = “unitStopped”
 *   id = <unit id>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class UnitStopped implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int unitid = message.get("id").asInt();
		
		gameState.setready(true);

		// check if there is an on-going attack waiting to be executed
		if (gameState.onGoingAttack()) {
			//if so, execute the attack
			gameState.attack(gameState.getAttacker(), gameState.getAttackTarget(), out);
			gameState.resetOngoingAttack();
		}
		
	}

}
