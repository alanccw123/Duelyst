package events;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.GameState;
import structures.basic.Tile;
import utils.AttackChecker;
import utils.MovementChecker;

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
		
		if (!gameState.isReady()) {
			return;
		}
		
		
		//the user clicks on a new tile
		if (gameState.unitLastClicked == null) {
			
			if (clicked.isHasUnit()) {
				
				//if the clicked tile is occupied, lists of tiles for movement & attack should be generated respectively
				List<Tile> range = MovementChecker.checkMovement(clicked, board);
				List<Tile> attackable = AttackChecker.checkAllAttackRange(range, board, clicked.getUnit().getPlayer());
				
				// highlight the tiles for movement in white
				for (Tile tile : range) {
					BasicCommands.drawTile(out, tile, 1);
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					gameState.highlighted.add(tile);
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
				
				// keep tracked of the unit & tile clicked
				gameState.unitLastClicked = clicked.getUnit();
				gameState.tilelastClicked = clicked;
				
			}
			
			
		}else {
			// if the user last clicked on an unit, this means the current clicked tile is a target for action
			
			
			// user clicks on a target for movement
			if (gameState.highlighted.contains(clicked)) {
				gameState.clearhighlight(out);
				gameState.moveUnit(gameState.unitLastClicked, clicked, out);	
			
			// user clicks on a target for attack
			}else if (gameState.highlightedForAttack.contains(clicked)) {
				gameState.clearhighlight(out);	
				gameState.attack(gameState.unitLastClicked, clicked.getUnit(), out);
			
			// user clicks on the same unit twice to cancelled the selection
			}else if (clicked == gameState.tilelastClicked) {
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
