package events;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;

import structures.Board;

import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.AttackChecker;
import utils.MovementChecker;
import views.html.defaultpages.todo;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		Board board = gameState.getGameBoard();
		
		Tile clicked = board.getTile(tilex, tiley);

		// do nothing if it is not player's turn
		if (!gameState.isPlayerTurn()) {
			return;
		}
		
		// do nothing when some moving animation is playing
		if (!gameState.isReady()) {
			return;
		}
		
		
		//the user clicks on a new tile
		if (gameState.unitLastClicked == null && gameState.cardLastClicked == null) {
			
			if (clicked.isHasUnit()) {

				// player clicks on an unit
				Unit selected = clicked.getUnit();

				// cannot operate on AI's units
				// if (selected.getPlayer() != 1) {
				// 	return;
				// }


				// check if the unit has movement action left
				if (selected.canMove()) {
					// generate lists of tiles for movement & attack
					List<Tile> range = MovementChecker.checkMovement(clicked, board); // tiles that the unit can move to
					List<Tile> attackable = AttackChecker.checkAllAttackRange(range, board, selected.getPlayer()); // tiles (with enemy unit) that can be attacked from tiles within movement range
					attackable.addAll(AttackChecker.checkAttackRange(clicked, board, selected.getPlayer()));// plus those that can be attacked from the unit current location

					// highlight the tiles for movement in white
					for (Tile tile : range) {
						BasicCommands.drawTile(out, tile, 1);
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						gameState.highlightedForMovement.add(tile);
					}
					
					// highlight the tiles for attack in red
					for (Tile tile : attackable) {
						BasicCommands.drawTile(out, tile, 2);
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						gameState.highlightedForAttack.add(tile);
					}

					// keep tracked of the unit selected
					gameState.unitLastClicked = selected;
					String debug = String.format("selected id%d x%d y%d", selected.getId(), clicked.getTilex(), clicked.getTiley());
					BasicCommands.addPlayer1Notification(out, debug, 2);

				// if the unit has moved but not yet attacked
				}else if (selected.canAttack()) {
					// only check 1 tile surrounding for target
					List<Tile> attackable = AttackChecker.checkAttackRange(clicked, board, selected.getPlayer());

					// should not select the unit if there is not valid target
					if (attackable.isEmpty()) {
						return;
					}

					// highlight the tiles for attack in red
					for (Tile tile : attackable) {
						BasicCommands.drawTile(out, tile, 2);
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						gameState.highlightedForAttack.add(tile);
					}

					// keep tracked of the unit selected
					gameState.unitLastClicked = selected;
	

					String debug = String.format("selected id%d x%d y%d", selected.getId(), clicked.getTilex(), clicked.getTiley());
					BasicCommands.addPlayer1Notification(out, debug, 2);
				}
				
				
			}
			
		// else if the player last selected a card
		}else if (gameState.cardLastClicked != null) {
			// play the card if the tile clicked is valid
			if (gameState.highlightedForCard.contains(clicked)) {
				gameState.clearhighlight(out);
				// remove card from hand
				int index = gameState.getCardPosition(gameState.cardLastClicked);
				gameState.removePlayerCard(index);
				gameState.displayHand(out);
				
				// execute the card's effects
				gameState.cardLastClicked.playCard(out, gameState, clicked);

				gameState.cardLastClicked = null;
			}
		}
		
		// else the user last selected an unit, this means the current clicked tile is a target for action
		else {
			
			// user clicks on a target for movement
			if (gameState.highlightedForMovement.contains(clicked)) {
				gameState.clearhighlight(out);
				gameState.moveUnit(gameState.unitLastClicked, clicked, out);	
			
			// user clicks on a target for attack
			}else if (gameState.highlightedForAttack.contains(clicked)) {
				gameState.clearhighlight(out);	
				gameState.attack(gameState.unitLastClicked, clicked.getUnit(), out);
			}

			// user clicks on the same unit twice to cancelled the selection
			else if (clicked == gameState.unitLastClicked.getTile()) {
				gameState.clearhighlight(out);

			// user clicks on a random tile, no action is performed
			}else {
				return;
			}
			
			//clear highlight and reference to the last clicked unit
			//so that the event-processor is ready to process a new action for another unit	
			gameState.unitLastClicked = null;
			
		}	
		

	}

}
