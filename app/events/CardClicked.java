package events;

import structures.GameState;
import structures.basic.*;

import java.util.List;

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

		// get the selected card
		Card selected = gameState.getPlayerCard(handPosition);
		
		// do nothing if it is not player's turn
		if (!gameState.isPlayerTurn()) {
			return;
		}
		
		// do nothing when some moving animation is playing
		if (!gameState.isReady()) {
			return;
		}

		//player selects a new card
		if (gameState.cardLastClicked == null) {

			// check if the player has enough mana
			if (selected.getManacost() > gameState.getHumanMana()) {
				return;
			}

			// get the list of target tiles for using the card
			List<Tile> tagets = selected.checkTargets(gameState, 1);

			// highlight the target tiles
			for (Tile tile : tagets) {
				// highlight enemy units in red
				if (tile.isHasUnit() && tile.getUnit().getPlayer() == 2) {
					BasicCommands.drawTile(out, tile, 2);
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				// highlight empty tiles and friendly units in white
				}else{
					BasicCommands.drawTile(out, tile, 1);
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// highligh selected card
				BasicCommands.drawCard(out, selected, handPosition, 1);
				// update card clicked
				gameState.highlightedForCard.add(tile);
				gameState.cardLastClicked = selected;
			}

		// the player clicks on a selected card to de-select
		}else if (selected == gameState.cardLastClicked) {
			BasicCommands.drawCard(out, selected, handPosition, 0);
			gameState.clearhighlight(out);
			gameState.cardLastClicked = null;
		}
			
		
		
	}
}
