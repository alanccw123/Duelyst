package utils;

import java.util.ArrayList;

import structures.Board;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;

/*
 * This utility class contains all the methods for doing movement related tiles calculation
 */

public class MovementChecker {
	
	
	// this method checks the moving range of an unit from the current tile
	// it returns a list of all tiles that the unit can go
	public static ArrayList<Tile> checkMovement(Tile tile, Board board) {
		ArrayList<Tile> range = new ArrayList<>();
		
		int x = tile.getTilex();
		int y = tile.getTiley();
		int player = tile.getUnit().getPlayer();

		// first, check whether there is enemy provoke unit adjacent
		for (Tile check : AttackChecker.checkAttackRange(tile, board, player)) {
			// if so, then this unit cannot move and therefore an empty list is returned
			if (check.getUnit().isProvoke()) {
				return range;
			}
		} 

		// if the unit is flying
		if (tile.getUnit().isflying()) {
			// return a list of all unoccupied tiles on board
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 5; j++) {
					if (!board.getTile(i, j).isHasUnit()) {
						range.add(board.getTile(i, j));
					}
				}
			}

			return range;
		}
		
		// check horizontal and vertical directions
		if (isEmpty(x, y + 1, board)) {
			range.add(board.getTile(x, y + 1));
		}
		if (isEmpty(x, y + 2, board) && isPassable(x, y + 1, board, player)) {
			range.add(board.getTile(x, y + 2));
		}
		
		
		if (isEmpty(x, y - 1, board)) {
			range.add(board.getTile(x, y - 1));
		}
		if (isEmpty(x, y - 2, board) && isPassable(x, y - 1, board, player)) {
			range.add(board.getTile(x, y - 2));
		}
		
		
		if (isEmpty(x + 1, y, board)) {
			range.add(board.getTile(x + 1, y));
		}
		if (isEmpty(x + 2, y, board) && isPassable(x + 1, y, board, player)) {
			range.add(board.getTile(x + 2, y));
		}
		
		
		if (isEmpty(x - 1, y, board)) {
			range.add(board.getTile(x - 1, y));
		}
		if (isEmpty(x - 2, y, board) && isPassable(x - 1, y, board, player)) {
			range.add(board.getTile(x - 2, y));
		}
		
		
		
		// check diagonal directions
		if (isEmpty(x + 1, y + 1, board) && (isPassable(x + 1, y, board, player) || isPassable(x, y + 1, board, player))) {
			range.add(board.getTile(x + 1, y + 1));
		}
		
		if (isEmpty(x + 1, y - 1, board) && (isPassable(x + 1, y, board, player) || isPassable(x, y - 1, board, player))) {
			range.add(board.getTile(x + 1, y - 1));
		}
		
		if (isEmpty(x - 1, y + 1, board) && (isPassable(x - 1, y, board, player) || isPassable(x, y + 1, board, player))) {
			range.add(board.getTile(x - 1, y + 1));
		}
		
		if (isEmpty(x - 1, y - 1, board)&& (isPassable(x - 1, y, board, player) || isPassable(x, y - 1, board, player))) {
			range.add(board.getTile(x - 1, y - 1));
		}
		
		return range;
	}
	
	// helper method to check if a tile is passable
	public static boolean isPassable(int x, int y, Board board, int player) {
		// out of bound tiles are not valid
		if (!withinBoard(x, y)) {
			return false;
		}
		
		// tiles being occupied by enemy units are not passable
		if (board.getTile(x, y).isHasUnit() && board.getTile(x, y).getUnit().getPlayer() != player) {
			return false;
		}
		
		// else the tile is passable 
		return true;
	}
	
	// helper function to check if a tile is unoccupied
	public static boolean isEmpty(int x, int y, Board board) {
		// check for out of bound and whether the tile is currently occupied
		if (withinBoard(x, y) && !board.getTile(x, y).isHasUnit()) {
			return true;
		}
		return false;
		
	}
	
	// helper function to check for out of bound
	public static boolean withinBoard(int x, int y) {
		if (x >= 0 && x <= 8 && y >= 0 && y <= 4) {
			return true;
		}
		return false;
	}
}
