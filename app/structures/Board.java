package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class Board {
	
	public Board() {
		this.board = new Tile[9][5];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = BasicObjectBuilders.loadTile(i, j);
			}
		}
	}
	
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
	
	public Tile getTile(int x, int y) {
		return board[x][y];
	}
	
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

	private Tile[][] board;
	
	
}
