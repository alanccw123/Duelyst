package structures;

import java.util.ArrayList;

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
	
	public ArrayList<Tile> highlighted = new ArrayList<>();
	
	public void clearhighlight(ActorRef out) {
		for (Tile tile : highlighted) {
			BasicCommands.drawTile(out, tile, 0);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		highlighted.clear();
	}
	
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
}
