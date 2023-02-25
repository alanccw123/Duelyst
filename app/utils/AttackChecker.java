package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import structures.Board;
import structures.basic.Tile;

public class AttackChecker {
	
	
	// check for viable targets for attacks within one Tile range
	public static List<Tile> checkAttackRange(Tile current, Board board, int player) {
		ArrayList<Tile> targets = new ArrayList<>();
		
		int x = current.getTilex();
		int y = current.getTiley();
		
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {

				Tile tile = null;
			
				if (MovementChecker.withinBoard(i, j)) {
					tile = board.getTile(i, j);
				}else {
					continue;
				}
				if (tile.isHasUnit() && tile.getUnit().getPlayer() != player) {
					targets.add(board.getTile(i, j));
				}
			}
		}
		
		return targets;
	}
	
	// check for all possible targets for attack given a list of possible tiles to move to
	public static List<Tile> checkAllAttackRange(List<Tile> tilesWithinRange, Board board, int player) {
		ArrayList<Tile> targets = new ArrayList<>();
		
		for (Tile tile : tilesWithinRange) {
			targets.addAll(checkAttackRange(tile, board, player));
		}
		
		return targets;
	}
}
