package structures;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.*;
import utils.MovementChecker;

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
	
	
	//attribute to keep track of whether moving animation is playing
	private boolean ready = true;
	
	
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
		
		//the default movement path is x-first
		//when the path is not passable i.e. blocked by an enemy unit, then y-first path should be taken
		if (target.getTilex() > x && !MovementChecker.isPassable(x + 1, y, gameBoard, unit.getPlayer())) {
			yFirst = true;
		}else if (target.getTilex() < x && !MovementChecker.isPassable(x - 1, y, gameBoard, unit.getPlayer())) {
			yFirst = true;
		}
		
		BasicCommands.moveUnitToTile(out, unit, target, yFirst);
		unit.setPositionByTile(target);
		target.setUnit(unit);
		tilelastClicked.removeUnit();
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void setready(boolean state) {
		ready = state;
	}
	

}
