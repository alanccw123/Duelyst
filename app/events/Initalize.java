package events;

import com.fasterxml.jackson.databind.JsonNode;
import utils.OrderedCardLoader;
import structures.GameState;
import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Player;
/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		gameState.humanTurn = true;
		gameState.something = true;
		gameState.initalize();
		 
		BasicCommands.setPlayer1Health(out, gameState.player);
		BasicCommands.setPlayer2Health(out, gameState.ai);
		BasicCommands.setPlayer1Mana(out, gameState.player);
		BasicCommands.setPlayer2Mana(out, gameState.ai);

	}

}


