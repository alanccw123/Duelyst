package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that a unit instance has started a move.
 * The event reports the unique id of the unit.
 * <p>
 * {
 * messageType = “unitMoving”
 * id = <unit id>
 * }
 *
 * @author Dr. Richard McCreadie
 */
<<<<<<< app/events/UnitMoving.java
public class UnitMoving implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        int unitid = message.get("id").asInt();

        gameState.setready(false);

    }
=======
public class UnitMoving implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int unitid = message.get("id").asInt();
		
		gameState.setready(false);
		
	}
>>>>>>> app/events/UnitMoving.java

}
