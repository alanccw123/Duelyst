package events;


import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.GameState;
import structures.basic.Tile;
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
		
		Board board = gameState.gameBoard;
		
		Tile clicked = board.getTile(tilex, tiley);
		

		if (gameState.unitLastClicked == null) {
			//generate a list of all tile within range
			if (clicked.isHasUnit()) {
				
				ArrayList<Tile> range = MovementChecker.checkMovement(clicked, board);
				
				for (Tile tile : range) {
					BasicCommands.drawTile(out, tile, 1);
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					gameState.highlighted.add(tile);
				}
//				ArrayList<int[]> range = new ArrayList<int[]>();
//				
//				for (int i = 1; i <= 2; i++) {
//					range.add(new int[] {tilex + i, tiley});
//					range.add(new int[] {tilex - i, tiley});
//					range.add(new int[] {tilex, tiley + i});
//					range.add(new int[] {tilex, tiley - i});
//				}
//				
//		
//				range.add(new int[] {tilex + 1, tiley + 1});
//				range.add(new int[] {tilex - 1, tiley - 1});
//				range.add(new int[] {tilex + 1, tiley - 1});
//				range.add(new int[] {tilex - 1, tiley + 1});
//
//				// check for out of bound and only get the valid tiles and render them in highlighted mode
//				for (int[] tile : range) {
//					if(tile[0] >= 0 && tile[0] <= 8 && tile[1] >= 0 && tile[1] <= 4 && !board.getTile(tile[0], tile[1]).isHasUnit()) {
//						BasicCommands.drawTile(out, board.getTile(tile[0], tile[1]), 1);
//						try {
//							Thread.sleep(5);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						gameState.highlighted.add(board.getTile(tile[0], tile[1]));
//					}
//				}
//				
				gameState.unitLastClicked = clicked.getUnit();
				gameState.tilelastClicked = clicked;
			}
			
			
		}else {
			
			if (gameState.highlighted.contains(clicked)) {
				gameState.clearhighlight(out);
				
				gameState.moveUnit(gameState.unitLastClicked, clicked, out);
//				BasicCommands.moveUnitToTile(out, gameState.unitLastClicked, clicked);
//				gameState.unitLastClicked.setPositionByTile(clicked);
//				clicked.setUnit(gameState.unitLastClicked);
//				gameState.tilelastClicked.removeUnit();
				
				gameState.unitLastClicked = null;
				
			}else if (clicked == gameState.tilelastClicked) {
				gameState.clearhighlight(out);		
				
				gameState.unitLastClicked = null;
			}			
			
		}
		
			
		
	}

}
