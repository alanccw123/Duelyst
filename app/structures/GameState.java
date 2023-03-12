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


	/* 
	turn change and Player related attributes & methods
	*/ 
	public boolean playerTurn = false;// boolean to keep track of whose turn it is
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
	

	// for testing demo
	public boolean gameInitalised = false;


	/* 
	cards and units related attributes
	*/ 
	private Board gameBoard = new Board();

	// lists storing the decks
	private List<Card> playerDeck = OrderedCardLoader.getPlayer1Cards();
	private List<Card> AIDeck = OrderedCardLoader.getPlayer2Cards();

	// lists storing the hands
	private List<Card> playerHand = new ArrayList<>();
	private List<Card> AIHand = new ArrayList<>();

	// Lists containing all units for player and AI respectively
	private List<Unit> playerUnits = new ArrayList<>();
	private List<Unit> AIUnits = new ArrayList<>();

	// draw a card from player deck to hand
	// return a boolean indicating whether the draw is successful
	public boolean playerDrawCard(ActorRef out) {
		if (playerDeck.isEmpty()) {
			// run of cards, player lose
			BasicCommands.addPlayer1Notification(out, "You lose! Your deck is out of cards", 10);
			return false;
		}
		Card card = playerDeck.remove(0);
		if (playerHand.size() < 6) {
			playerHand.add(card);
			return true;
		}
		return false;
	}

	public List<Card> getPlayerHand() {
		return playerHand;
	}

	// draw a card from AI deck to hand
	// return a boolean indicating whether the draw is successful
	public boolean AIDrawCard(ActorRef out) {
		if (AIDeck.isEmpty()) {
			// run of cards, ai lose
			BasicCommands.addPlayer1Notification(out, "You win! Enemy deck is out of cards", 10);
			return false;
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

	public void removePlayerCard(Card card) {
		playerHand.remove(card);
	}

	public void removeAICard(int index) {
		AIHand.remove(index - 1);
	}

	public void removeAICard(Card card) {
		AIHand.remove(card);
	}

	public Card getPlayerCard(int index) {
		return playerHand.get(index - 1);
	}

	public List<Card> getAIHand() {
		return AIHand;
	}

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

	/* keep track of the objects last-clicked 
	these values are useful in the eventprocessors'logic
	*/
	public Unit unitLastClicked;
	public Tile tileLastClicked;
	public Card cardLastClicked;
	
	// keep track of the highlighted tiles for different actions 
	public List<Tile> highlightedForMovement = new ArrayList<>();
	public List<Tile> highlightedForAttack = new ArrayList<>();
	public List<Tile> highlightedForCard = new ArrayList<>();
	
	//boolean to keep track of whether moving animation is playing
	private boolean ready = true;

	//boolean to keep track of whether there is an attack to do after an unit stopped moving
	private boolean onGoingAttack = false;


	private Unit defender;
	private Unit attacker;

	public boolean onGoingAttack() {
		return onGoingAttack;
	}

	public Unit getAttackTarget() {
		return defender;
	}

	public Unit getAttacker() {
		return attacker;
	}

	public void resetOngoingAttack() {
		onGoingAttack=false;
		defender=null;
		attacker=null;
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
	
	/* 
	This method move an unit to a given tile
	 */
	public void moveUnit(Unit unit, Tile target, ActorRef out) {
		// spend movement action
		unit.spendMoveAction();

		// get the unit current position
		Tile current = gameBoard.searchFor(unit);
		int x = current.getTilex();
		int y = current.getTiley();
		
		//the default movement path is x-first
		//when the path is not passable i.e. blocked by an enemy unit, then y-first path should be taken
		boolean yFirst = false;
		if (target.getTilex() > x && !MovementChecker.isPassable(x + 1, y, gameBoard, unit.getPlayer())) {
			yFirst = true;
		}else if (target.getTilex() < x && !MovementChecker.isPassable(x - 1, y, gameBoard, unit.getPlayer())) {
			yFirst = true;
		}
		
		// play moving animation in the frontend
		BasicCommands.moveUnitToTile(out, unit, target, yFirst);

		//update unit's new position
		current.removeUnit();
		unit.setPositionByTile(target);

	}
	
	/* This is the method for performing an attack
	 It takes the attcker and defender units as input
	 and does all the relevant calculation
	 */
	public void attack(Unit attacker, Unit defender, ActorRef out) {
		
		//spend attack action
		if (!onGoingAttack) {
			attacker.spendAttackAction();
		}

		Tile current = attacker.getTile();
		Tile target = defender.getTile();
		
		//if the attack target is out of reach, need to move the unit into position first before attack
		if (!AttackChecker.checkAttackRange(current, gameBoard, attacker.getPlayer()).contains(target)) {
					
			// decide which tile to move to in order to launch the attack
			List<Tile> possibleTiles = MovementChecker.checkMovement(current, gameBoard);
			int shortestDistance = 0;
			Tile destination = null;
			
			// the shortest path that would allow the unit to attack is picked
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
					// therefore, the unit would prioritise moving right over left, and down over top in this case
					}else if (distance == shortestDistance) {
						if (destination.getTilex() - current.getTilex()+ destination.getTiley() - current.getTiley() < (xDistance + yDistance)) {
							destination = tile;
						}
					}
				}
				
			}
			// moving the unit into attack position
			moveUnit(attacker, destination, out);

			// keep track of the on-going unfinished attack
			onGoingAttack = true;
			this.defender = defender;
			this.attacker = attacker;

		// else if the target is now in range
		}else {
			
			// attacker attack
			BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
			//play projectile animation if the attacker is ranged
			if (attacker.isRanged()) {
				EffectAnimation projectile = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
				BasicCommands.playProjectileAnimation(out, projectile, 11, current, target);
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.idle);

			//defender counter-attack if not dead, haven't countered this turn and within range
			if (unitTakeDamage(defender, out, attacker.getAttack()) && defender.canCounterAttack() && AttackChecker.checkCounterAttack(target, gameBoard).contains(attacker.getTile())) {
				
				BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.attack);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				unitTakeDamage(attacker, out, defender.getAttack());
				defender.counterAttack();

				BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.idle);				
			}
			
		}	
				
	}
	
	/*
	 * This method does all caculation for dealing damage to an unit from any sources
	 *  It returns a boolean indicating whether the unit survives the damage
	 */
	public boolean unitTakeDamage(Unit unit, ActorRef out, int damage) {
		
		// if the damaged unit is avatar
		if (unit.getId() == 99) {
			for (Unit friendly : playerUnits) {
				// increment attack of silverguard knight
				if (friendly.getId() == 3 || friendly.getId() == 10) {
					friendly.setAttack(friendly.getAttack() + 2);
					BasicCommands.setUnitAttack(out, friendly, friendly.getAttack());
				}
			}
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
			// if the dying unit is windstrike
			if (unit.getId() == 24 || unit.getId() == 34) {
				// AI draws a card
				AIDrawCard(out);
			}

			// remove the dead unit
			BasicCommands.deleteUnit(out, unit);
			unit.getTile().removeUnit();
			playerUnits.remove(unit);
			AIUnits.remove(unit);
			return false;
		}else {
			// the unit survives
			BasicCommands.setUnitHealth(out, unit, unit.getHealth());
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);
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
