package utils;

import java.util.ArrayList;

import structures.Board;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;

public class MovementChecker {
	
	public static ArrayList<Tile> checkMovement(Tile tile, Board board) {
		ArrayList<Tile> range = new ArrayList<>();
		
		int x = tile.getTilex();
		int y = tile.getTiley();
		int player = tile.getUnit().getPlayer();
		
	
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
		
//		if (withinBoard(x,y + 1) && !board.getTile(x, y + 1).isHasUnit()) {
//			range.add(board.getTile(x, y + 1));
//			if (withinBoard(x,y + 2) && !board.getTile(x, y + 2).isHasUnit()) {
//				range.add(board.getTile(x, y + 2));
//			}
//		}
//		
//		if (withinBoard(x,y - 1) && !board.getTile(x, y - 1).isHasUnit()) {
//			range.add(board.getTile(x, y - 1));
//			if (withinBoard(x,y - 2) && !board.getTile(x, y - 2).isHasUnit()) {
//				range.add(board.getTile(x, y - 2));
//			}
//		}
//		
//		if (withinBoard(x + 1, y) && !board.getTile(x + 1, y).isHasUnit()) {
//			range.add(board.getTile(x + 1, y));
//			if (withinBoard(x + 2,y) && !board.getTile(x + 2, y).isHasUnit()) {
//				range.add(board.getTile(x + 2, y));
//			}
//		}
//		
//		if (withinBoard(x - 1, y) && !board.getTile(x - 1, y).isHasUnit()) {
//			range.add(board.getTile(x - 1, y));
//			if (withinBoard(x - 2,y) && !board.getTile(x - 2, y).isHasUnit()) {
//				range.add(board.getTile(x - 2, y));
//			}
//		}
//		
//		if (withinBoard(x + 1, y + 1) && !board.getTile(x + 1, y + 1).isHasUnit() && !(board.getTile(x + 1, y).isHasUnit() && board.getTile(x, y + 1).isHasUnit())) {
//			range.add(board.getTile(x + 1, y + 1));
//		}
//		
//		if (withinBoard(x + 1, y - 1) && !board.getTile(x + 1, y - 1).isHasUnit() && !(board.getTile(x + 1, y).isHasUnit() && board.getTile(x, y - 1).isHasUnit())) {
//			range.add(board.getTile(x + 1, y - 1));
//		}
//		
//		if (withinBoard(x - 1, y + 1) && !board.getTile(x - 1, y + 1).isHasUnit() && !(board.getTile(x - 1, y).isHasUnit() && board.getTile(x, y + 1).isHasUnit())) {
//			range.add(board.getTile(x - 1, y + 1));
//		}
//		
//		if (withinBoard(x - 1, y - 1) && !board.getTile(x - 1, y - 1).isHasUnit() && !(board.getTile(x - 1, y).isHasUnit() && board.getTile(x, y - 1).isHasUnit())) {
//			range.add(board.getTile(x - 1, y - 1));
//		}

		return range;
	}
	
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
	
	public static boolean isEmpty(int x, int y, Board board) {
		// check for out of bound and whether the tile is currently occupied
		if (withinBoard(x, y) && !board.getTile(x, y).isHasUnit()) {
			return true;
		}
		return false;
		
	}
	
	public static boolean withinBoard(int x, int y) {
		if (x >= 0 && x <= 8 && y >= 0 && y <= 4) {
			return true;
		}
		return false;
	}
}
