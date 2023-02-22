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
		gameState.sethumanBoard(1,2,StaticConfFiles.humanAvatar);
		gameState.setHandCard(0, 0);
		gameState.setHandCard(1, 1);
		gameState.setHandCard(2, 2);
		Board board = new Board();
		board.showBoard(out,gameState); 
		BasicCommands.setPlayer1Health(out, gameState.humanPlayer);
		BasicCommands.setPlayer2Health(out, gameState.aiPlayer);
		BasicCommands.setPlayer1Mana(out, gameState.humanPlayer);
		BasicCommands.setPlayer2Mana(out, gameState.aiPlayer);
		Card No1 = gameState.getHandCard(0);
		Card No2 = gameState.getHandCard(1);
		Card No3 = gameState.getHandCard(2);
		BasicCommands.drawCard(out, No1, 1, 0);
		BasicCommands.drawCard(out, No2, 2, 0);
		BasicCommands.drawCard(out, No3, 3, 0);
	}

}


