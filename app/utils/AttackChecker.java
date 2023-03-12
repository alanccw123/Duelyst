package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import structures.Board;
import structures.basic.Tile;
import structures.basic.Unit;

/*
 * This utility class contains all the methods for doing attack related tiles calculation
 */

public class AttackChecker {
	
	
	// check for viable targets for attacks for an unit
	public static List<Tile> checkAttackRange(Tile current, Board board, int player) {
		List<Tile> targets = new ArrayList<>();

		int x = current.getTilex();
		int y = current.getTiley();

		
		// add all tiles occupied by enemy units within normal attack range to the list
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

		// check for presence of provoke units
		List<Tile> provoke = new ArrayList<>();
		for (Tile tile : targets) {
			if (tile.getUnit().isProvoke()) {
				provoke.add(tile);
			}
		}
		
		// if there is no provoke unit, return all tiles
		if (provoke.isEmpty()) {
			// return ranged targets for ranged unit
			if (current.isHasUnit() && current.getUnit().isRanged()) {
				return rangedAttack(current, board, player);
			}
			return targets;
		}else {
		// else only return the tiles with provoke unit
			return provoke;
		}
		
	}
	
	// check for all possible targets for attack given a list of possible tiles to move to
	public static List<Tile> checkAllAttackRange(List<Tile> tilesWithinRange, Board board, int player) {
		ArrayList<Tile> targets = new ArrayList<>();
		
		for (Tile tile : tilesWithinRange) {
			targets.addAll(checkAttackRange(tile, board, player));
		}
		
		return targets;
	}

	// check for targets on the entire board for ranged units
	public static List<Tile> rangedAttack(Tile current, Board board, int player) {
		List<Tile> rangedTargets = new ArrayList<>();

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				Tile tile = board.getTile(i, j);
				if (tile.isHasUnit() && tile.getUnit().getPlayer() != player) {
					rangedTargets.add(tile);
				}
			}
		}

		return rangedTargets;
	}

	// check for counter-attack range, disregarding provoke effects
	public static List<Tile> checkCounterAttack(Tile current, Board board) {
		List<Tile> range = new ArrayList<>();
		int x = current.getTilex();
		int y = current.getTiley();

		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {

				if (MovementChecker.withinBoard(i, j)) {
					range.add(board.getTile(i,j));
				}
			}
		}

		return range;
	}
}
