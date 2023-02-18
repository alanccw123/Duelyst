package utils;

import java.util.ArrayList;

import structures.Board;
import structures.GameState;
import structures.basic.Tile;

public class MovementChecker {
	
	public static ArrayList<Tile> checkMovement(Tile tile, Board board) {
		ArrayList<Tile> range = new ArrayList<>();
		
		int x = tile.getTilex();
		int y = tile.getTiley();
		
		if (withinBoard(x,y + 1) && !board.getTile(x, y + 1).isHasUnit()) {
			range.add(board.getTile(x, y + 1));
			if (withinBoard(x,y + 2) && !board.getTile(x, y + 2).isHasUnit()) {
				range.add(board.getTile(x, y + 2));
			}
		}
		
		if (withinBoard(x,y - 1) && !board.getTile(x, y - 1).isHasUnit()) {
			range.add(board.getTile(x, y - 1));
			if (withinBoard(x,y - 2) && !board.getTile(x, y - 2).isHasUnit()) {
				range.add(board.getTile(x, y - 2));
			}
		}
		
		if (withinBoard(x + 1, y) && !board.getTile(x + 1, y).isHasUnit()) {
			range.add(board.getTile(x + 1, y));
			if (withinBoard(x + 2,y) && !board.getTile(x + 2, y).isHasUnit()) {
				range.add(board.getTile(x + 2, y));
			}
		}
		
		if (withinBoard(x - 1, y) && !board.getTile(x - 1, y).isHasUnit()) {
			range.add(board.getTile(x - 1, y));
			if (withinBoard(x - 2,y) && !board.getTile(x - 2, y).isHasUnit()) {
				range.add(board.getTile(x - 2, y));
			}
		}
		
		if (withinBoard(x + 1, y + 1) && !board.getTile(x + 1, y + 1).isHasUnit() && !(board.getTile(x + 1, y).isHasUnit() && board.getTile(x, y + 1).isHasUnit())) {
			range.add(board.getTile(x + 1, y + 1));
		}
		
		if (withinBoard(x + 1, y - 1) && !board.getTile(x + 1, y - 1).isHasUnit() && !(board.getTile(x + 1, y).isHasUnit() && board.getTile(x, y - 1).isHasUnit())) {
			range.add(board.getTile(x + 1, y - 1));
		}
		
		if (withinBoard(x - 1, y + 1) && !board.getTile(x - 1, y + 1).isHasUnit() && !(board.getTile(x - 1, y).isHasUnit() && board.getTile(x, y + 1).isHasUnit())) {
			range.add(board.getTile(x - 1, y + 1));
		}
		
		if (withinBoard(x - 1, y - 1) && !board.getTile(x - 1, y - 1).isHasUnit() && !(board.getTile(x - 1, y).isHasUnit() && board.getTile(x, y - 1).isHasUnit())) {
			range.add(board.getTile(x - 1, y - 1));
		}

		return range;
	}
	
	public static boolean withinBoard(int x, int y) {
		if (x >= 0 && x <= 8 && y >= 0 && y <= 4) {
			return true;
		}
		return false;
	}
}
