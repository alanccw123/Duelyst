package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

/*This class represents the gameboard and store the data structure, which is a 2D array of tile objects
 * 
 */

public class Board {
	
	public Board() {
		this.board = new Tile[9][5];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = BasicObjectBuilders.loadTile(i, j);
			}
		}
	}
	
	// render the board in the frontend
	public void initialize(ActorRef out) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				BasicCommands.drawTile(out, board[i][j], 0);
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// return the Tile object for a given x and y position
	public Tile getTile(int x, int y) {
		return board[x][y];
	}
	
	// return the tile that a given unit is currently on
	// if the unit cannot be found, return null
	public Tile searchFor(Unit unit) {
		
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j].isHasUnit() && board[i][j].getUnit().equals(unit)) {
					return board[i][j];
				}
			}
		}
		return null;
	}

	public Tile[][] board;
	
	
}
