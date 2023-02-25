package structures;


import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.*;
import utils.AttackChecker;
import utils.MovementChecker;


/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {


	
	public boolean humanTurn = false;
	
	public boolean something = false;

	private int humanStep;
	private int aiStep;
	public Player player;
	public Player ai;


	public void initalize(){
	}

	
	
	public int getHumanHealth() {
		return player.getHealth();
	}
	public void setHumanHealth(int a) {
		if(a > 20) {
			player.setHealth(20);
		}else {
			player.setHealth(a);
		}
	}
	public int getHumanMana() {
		return player.getMana();
	}
	public void setHumanMana(int mana) {
		if(mana >9) {
			player.setMana(9);
		}else {
			player.setMana(mana);
		}
	}
	
	
	public int getAiHealth() {
		return ai.getHealth();
	}
	public void setAiHealth(int a) {
		if(a > 20) {
			ai.setHealth(20);
		}else {
			ai.setHealth(a);
		}
	}
	public int getAiMana() {
		return ai.getMana();
	}
	public void setAiMana(int mana) {
		if(mana >9) {
			ai.setMana(9);
		}else {
			ai.setMana(mana);
		}
	}
	
	
	
	
	public void addHumanStep(int step) {
		humanStep = humanStep + step; 
	}
	public void addAiStep(int step) {
		humanStep =+ step; 
	}
	
	
	public int getHumanStep() {
		return humanStep;
	}
	public int getAiStep() {
		return aiStep;
	}
	
	
	public void setHumanStep(int n) {
		this.humanStep = n;
	}
	public void setAiStep(int n) {
		this.aiStep = n;
	}

	// for testing demo
	public boolean gameInitalised = false;
	public boolean something;
	
	private Board gameBoard = new Board();

	private Player player;
	private Player ai;
	
	public Unit unitLastClicked;
	
	public Tile tilelastClicked;
	
	public List<Tile> highlighted = new ArrayList<>();
	public List<Tile> highlightedForAttack = new ArrayList<>();
	
	//attribute to keep track of whether moving animation is playing
	private boolean ready = true;

	private int unitID = 0;
	
	
	public int getUnitID() {
        return unitID++;
    }

	public Board getGameBoard() {
		return gameBoard;
	}

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
		Tile current = gameBoard.searchFor(unit);
		int x = current.getTilex();
		int y = current.getTiley();
		boolean yFirst = false;
		
		//the default movement path is x-first
		//when the path is not passable i.e. blocked by an enemy unit, then y-first path should be taken
		if (target.getTilex() > x && !MovementChecker.isPassable(x + 1, y, gameBoard, unit.getPlayer())) {
			yFirst = true;
		}else if (target.getTilex() < x && !MovementChecker.isPassable(x - 1, y, gameBoard, unit.getPlayer())) {
			yFirst = true;
		}
		
		BasicCommands.moveUnitToTile(out, unit, target, yFirst);
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		current.removeUnit();
		unit.setPositionByTile(target);

	}
	
	// helper method to perform an attack
	public void attack(Unit attacker, Unit defender, ActorRef out) {
		
		Tile current = gameBoard.searchFor(attacker);
		Tile target = gameBoard.searchFor(defender);
		
		//if the attack target is out of reach
		if (!AttackChecker.checkAttackRange(current, gameBoard, attacker.getPlayer()).contains(target)) {
			
			//need to move the unit into position first before attack
			List<Tile> possibleTiles = MovementChecker.checkMovement(current, gameBoard);
			
			
			int shortestDistance = 0;
			Tile destination = null;
			
			for (Tile tile : possibleTiles) {
				//check if moving to this tile allows the attacker to initiate attack
				if (AttackChecker.checkAttackRange(tile, gameBoard, attacker.getPlayer()).contains(target)) {
					// if so, check the distance (no of tiles away)
					int xDistance = tile.getTilex() - current.getTilex();
					int yDistance = tile.getTiley() - current.getTiley();
					int distance = Math.abs(xDistance) + Math.abs(yDistance);
					
					// keep track of the shortest path
					if (shortestDistance == 0 || distance < shortestDistance) {
						shortestDistance = distance;
						destination = tile;
						
					// if two paths have equal distance, the one with positive difference in position indices would be used
					// therefore, the unit would prioritise moving right over left, and down over top
					}else if (distance == shortestDistance) {
						if (destination.getTilex() - current.getTilex()+ destination.getTiley() - current.getTiley() < (xDistance + yDistance)) {
							destination = tile;
						}
					}
				}
				
			}
			// moving the unit into attack position
			moveUnit(attacker, destination, out);
		}
		
		
		
		// attacker attack
		BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (unitTakeDamage(defender, out, attacker.getAttack())) {
			//defender counter-attack if not dead
			BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.attack);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			unitTakeDamage(attacker, out, defender.getAttack());
		}	
				
	}
	
	// helper method for dealing damage to an unit 
	// returns a boolean indicating whether the unit survives the damage
	public boolean unitTakeDamage(Unit unit, ActorRef out, int damage) {
		unit.setHealth(unit.getHealth() - damage);
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.hit);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (unit.getHealth() <= 0) {
			// the unit is dead
			BasicCommands.setUnitHealth(out, unit, 0);
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BasicCommands.deleteUnit(out, unit);
			gameBoard.searchFor(unit).removeUnit();
			return false;
		}else {
			// the unit survives
			BasicCommands.setUnitHealth(out, unit, unit.getHealth());
			return true;
		}
		
	}
	public boolean isReady() {
		return ready;
	}
	
	public void setready(boolean state) {
		ready = state;
	}
	


}
