package structures;


import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.*;
import utils.AttackChecker;
import utils.BasicObjectBuilders;
import utils.MovementChecker;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;


/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {


	// turn and Player related attributes
	public boolean playerTurn = false;
	private int turnNum = 1;
	private Player player = new Player(20, 0);
	private Player ai = new Player(20, 0);


	public Player getPlayer() {
		return player;
	}
	public Player getAi() {
		return ai;
	}
	public int getTurnNum() {
		return turnNum;
	}

	public boolean isPlayerTurn() {
		return playerTurn;
	}

	public void changeTurn() {
		playerTurn = !playerTurn;
	}

	public void incrementTurn() {
		turnNum++;
	}

	// reset attack & movement actions for all units
	public void resetAllAction() {
		for (Unit unit : playerUnits) {
			unit.resetAction();
		}
		for (Unit unit : AIUnits) {
			unit.resetAction();
		}
	}



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
	
	
	
	// public boolean something = false;
	// private int humanStep;
	// private int aiStep;
	// public void addHumanStep(int step) {
	// 	humanStep = humanStep + step; 
	// }
	// public void addAiStep(int step) {
	// 	humanStep =+ step; 
	// }
	
	
	// public int getHumanStep() {
	// 	return humanStep;
	// }
	// public int getAiStep() {
	// 	return aiStep;
	// }
	
	
	// public void setHumanStep(int n) {
	// 	this.humanStep = n;
	// }
	// public void setAiStep(int n) {
	// 	this.aiStep = n;
	// }

	// for testing demo
	public boolean gameInitalised = false;


	// cards and units related attributes
	private Board gameBoard = new Board();

	private List<Card> playerDeck = OrderedCardLoader.getPlayer1Cards();
	private List<Card> AIDeck = OrderedCardLoader.getPlayer2Cards();

	private List<Card> playerHand = new ArrayList<>();
	private List<Card> AIHand = new ArrayList<>();

	// Lists containing all units for player and AI respectively
	private List<Unit> playerUnits = new ArrayList<>();
	private List<Unit> AIUnits = new ArrayList<>();

	// draw a card from player deck to hand
	// return a boolean indicating whether the draw is successful
	public boolean playerDrawCard() {
		if (playerDeck.isEmpty()) {
			// run of cards, player lose
		}
		Card card = playerDeck.remove(0);
		if (playerHand.size() < 6) {
			playerHand.add(card);
			return true;
		}
		return false;
	}

	
	// draw a card from AI deck to hand
	// return a boolean indicating whether the draw is successful
	public boolean AIDrawCard() {
		if (AIDeck.isEmpty()) {
			// run of cards, ai lose
		}
		Card card = AIDeck.remove(0);
		if (AIHand.size() < 6) {
			AIHand.add(card);
			return true;
		}
		return false;
	}

	// display the updated hand of player on the frontend
	// call this method everytime after hand is changed
	public void displayHand(ActorRef out) {
		int counter = 1;
		for (Card card : playerHand) {
			BasicCommands.drawCard(out, card, counter, 0);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			counter++;
		}
		for (int i = counter; i <= 6; i++) {
			BasicCommands.deleteCard(out, i);
		}
	}

	public int getCardPosition(Card card) {
		return playerHand.indexOf(card) + 1;
	}

	public void removePlayerCard(int index) {
		playerHand.remove(index - 1);
	}

	public void removeAICard(int index) {
		AIHand.remove(index - 1);
	}

	public Card getPlayerCard(int index) {
		return playerHand.get(index - 1);
	}

	// public void summonPlayerUnit(Unit unit, Tile tile, ActorRef out) {
	// 	EffectAnimation summon = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
	// 	BasicCommands.playEffectAnimation(out, summon, tile);
	// 	BasicCommands.drawUnit(out, unit, tile);
	// 	try {
	// 		Thread.sleep(5);
	// 	} catch (InterruptedException e) {
	// 		e.printStackTrace();
	// 	}
	// 	BasicCommands.setUnitAttack(out, unit, unit.getAttack());
	// 	try {
	// 		Thread.sleep(5);
	// 	} catch (InterruptedException e) {
	// 		e.printStackTrace();
	// 	}
	// 	BasicCommands.setUnitHealth(out, unit, unit.getHealth());
	// 	try {
	// 		Thread.sleep(5);
	// 	} catch (InterruptedException e) {
	// 		e.printStackTrace();
	// 	}
	// 	playerUnits.add(unit);
	// 	unit.setPlayer(1);
	// 	unit.setPositionByTile(tile);		
	// }

	// public void summonAIUnit(Unit unit, Tile tile, ActorRef out) {
	// 	EffectAnimation summon = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
	// 	BasicCommands.playEffectAnimation(out, summon, tile);
	// 	BasicCommands.drawUnit(out, unit, tile);
	// 	try {
	// 		Thread.sleep(5);
	// 	} catch (InterruptedException e) {
	// 		e.printStackTrace();
	// 	}
	// 	BasicCommands.setUnitAttack(out, unit, unit.getAttack());
	// 	try {
	// 		Thread.sleep(5);
	// 	} catch (InterruptedException e) {
	// 		e.printStackTrace();
	// 	}
	// 	BasicCommands.setUnitHealth(out, unit, unit.getHealth());
	// 	try {
	// 		Thread.sleep(5);
	// 	} catch (InterruptedException e) {
	// 		e.printStackTrace();
	// 	}
	// 	AIUnits.add(unit);
	// 	unit.setPlayer(2);
	// 	unit.setPositionByTile(tile);	
	// }

	public void addPlayerUnit(Unit unit) {
		playerUnits.add(unit);
	}

	public void addAIUnit(Unit unit) {
		AIUnits.add(unit);
	}

	public void removePlayerUnit(Unit unit) {
		playerUnits.remove(unit);
	}

	public void removeAIUnit(Unit unit) {
		AIUnits.remove(unit);
	}

	public List<Unit> getPlayerUnits() {
		return playerUnits;
	}
	
	public List<Unit> getAIUnits() {
		return AIUnits;
	}

	// keep track of the objects last-clicked
	// these values are useful in the eventprocessors'logic
	public Unit unitLastClicked;
	public Tile tileLastClicked;
	public Card cardLastClicked;
	
	// keep track of the highlighted tiles on board
	public List<Tile> highlightedForMovement = new ArrayList<>();
	public List<Tile> highlightedForAttack = new ArrayList<>();
	public List<Tile> highlightedForCard = new ArrayList<>();
	
	//attribute to keep track of whether moving animation is playing
	private boolean ready = true;

	// not used
	private int unitID = 0;
	
	public int getUnitID() {
        return unitID++;
    }

	public Board getGameBoard() {
		return gameBoard;
	}

    // helper method to de-highlight all tiles
	public void clearhighlight(ActorRef out) {
		for (Tile tile : highlightedForMovement) {
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
		for (Tile tile : highlightedForCard) {
			BasicCommands.drawTile(out, tile, 0);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		highlightedForMovement.clear();
		highlightedForAttack.clear();
		highlightedForCard.clear();
	}
	
	// move an unit to a given tile
	public void moveUnit(Unit unit, Tile target, ActorRef out) {
		// spend movement action
		unit.spendMoveAction();
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

		//update unit's new position
		current.removeUnit();
		unit.setPositionByTile(target);

	}
	
	// perform an attack
	// takes the attcker and defender units as input
	public void attack(Unit attacker, Unit defender, ActorRef out) {
		//spend attacker's action
		attacker.spendAttackAction();
		
		// Tile current = gameBoard.searchFor(attacker);
		// Tile target = gameBoard.searchFor(defender);

		Tile current = attacker.getTile();
		Tile target = defender.getTile();
		
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

		//defender counter-attack if not dead, haven't countered this turn and within range
		if (unitTakeDamage(defender, out, attacker.getAttack()) && defender.canAttack() && AttackChecker.checkAttackRange(defender.getTile(), gameBoard, defender.getPlayer()).contains(current)) {
			
			BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.attack);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			unitTakeDamage(attacker, out, defender.getAttack());
			defender.spendAttackAction();
		}	
				
	}
	
	// helper method for dealing damage to an unit 
	// returns a boolean indicating whether the unit survives the damage
	public boolean unitTakeDamage(Unit unit, ActorRef out, int damage) {
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.hit);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		unit.setHealth(unit.getHealth() - damage);
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
			playerUnits.remove(unit);
			AIUnits.remove(unit);
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
