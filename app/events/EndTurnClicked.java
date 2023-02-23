package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;

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

			gameState.humanTurn = false;
			gameState.something = false;
//结束抽牌
			boolean handFull = true;
			for (int n = 0; n < 6; n++) {
				if (gameState.getHandCard(n) == null) {
					handFull = false;
					gameState.setHandCard(n, gameState.cardId);
					gameState.cardId++;
					Card getCard = gameState.getHandCard(n);
					BasicCommands.drawCard(out, getCard, n+1, 0);
					break;
				}
			}
			if (handFull) {
				BasicCommands.addPlayer1Notification(out, "loss card", 2);
			}
//下面ai不用管
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

			gameState.humanTurn = true;
			gameState.something = true;
			BasicCommands.addPlayer1Notification(out, "Your turn", 2);
		}
	}
}