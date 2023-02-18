package structures;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.http.scaladsl.model.HttpEntity.LastChunk;
import commands.BasicCommands;
import structures.basic.*;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	
	public Board gameBoard;
	
	public Player player;
	
	public Player ai;
	
	public boolean something;
	
	public Unit avatar1;
	
	public Unit unitLastClicked;
	public Tile tilelastClicked;
	
	public List<Tile> highlighted = new ArrayList<>();
	
	public List<Tile> highlightedForAttack = new ArrayList<>();
	
	
	// helper method to de-highlight all tiles
	public void clearhighlight(ActorRef out) {
		for (Tile tile : highlighted) {
			BasicCommands.drawTile(out, tile, 0);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for (Tile tile : highlightedForAttack) {
			BasicCommands.drawTile(out, tile, 0);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		highlighted.clear();
		highlightedForAttack.clear();
	}
	
	// helper method to move an unit to a given tile
	public void moveUnit(Unit unit, Tile target, ActorRef out) {
		int x = tilelastClicked.getTilex();
		int y = tilelastClicked.getTiley();
		boolean yFirst = false;
		
		if (target.getTilex() > x && gameBoard.getTile(x + 1, y).isHasUnit()) {
			yFirst = true;
		}else if (target.getTilex() < x && gameBoard.getTile(x - 1, y).isHasUnit()) {
			yFirst = true;
		}
		
		BasicCommands.moveUnitToTile(out, unit, target, yFirst);
		unit.setPositionByTile(target);
		target.setUnit(unit);
		tilelastClicked.removeUnit();
	}
	
	// a method to perform all all calculations in an attack
	public void attack(Unit attacker, Unit defender, ActorRef out) {
		BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
		
		if (defender.getHealth() <= attacker.getAttack()) {
			//defender is dead
			BasicCommands.setUnitHealth(out, defender, 0);
			BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.death);
			BasicCommands.deleteUnit(out, defender);
			gameBoard.searchFor(defender).removeUnit();
		}else {
			defender.setHealth(defender.getHealth() - attacker.getAttack());
			BasicCommands.setUnitHealth(out, defender, defender.getHealth());
		}		
				
	}
}
