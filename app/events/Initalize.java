package events;

import com.fasterxml.jackson.databind.JsonNode;


import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import structures.Board;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;


/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

<<<<<<< app/events/Initalize.java
=======

>>>>>>> app/events/Initalize.java
		gameState.changeTurn();
		// gameState.something = true;
		gameState.initalize();
		// BasicCommands.setPlayer1Health(out, gameState.player);
		// BasicCommands.setPlayer2Health(out, gameState.ai);
		// BasicCommands.setPlayer1Mana(out, gameState.player);
		// BasicCommands.setPlayer2Mana(out, gameState.ai);
<<<<<<< app/events/Initalize.java
=======


>>>>>>> app/events/Initalize.java
		
		gameState.gameInitalised = true;
		
		Board gameBoard = gameState.getGameBoard();
		
		// render the board on the frontend
		gameBoard.initialize(out);
		
		// place player's avatar
		Unit avatar1 = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 99, Unit.class);
		Tile initial1 = gameBoard.getTile(1,2);
<<<<<<< app/events/Initalize.java
		assert avatar1 != null;
=======
>>>>>>> app/events/Initalize.java
		avatar1.setPositionByTile(initial1);
		avatar1.setPlayer(1);
		avatar1.setAttack(2);
		avatar1.setHealth(20);
		avatar1.resetAction();
		gameState.addPlayerUnit(avatar1);
		
		BasicCommands.drawUnit(out, avatar1, initial1);
		try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, avatar1, avatar1.getAttack());
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, avatar1, avatar1.getHealth());
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		
		// place AI's avatar
		Unit avatar2 = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 100, Unit.class);
		Tile initial2 = gameBoard.getTile(7,2);
<<<<<<< app/events/Initalize.java
		assert avatar2 != null;
=======
>>>>>>> app/events/Initalize.java
		avatar2.setPositionByTile(initial2);
		avatar2.setPlayer(2);
		avatar2.setAttack(2);
		avatar2.setHealth(20);
		avatar2.resetAction();
		gameState.addAIUnit(avatar2);
		
		BasicCommands.drawUnit(out, avatar2, initial2);
		try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, avatar2, avatar2.getAttack());
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, avatar2, avatar2.getHealth());
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
<<<<<<< app/events/Initalize.java

=======
		
		
		
>>>>>>> app/events/Initalize.java
		
		// this avatar is for testing
		// Unit avatar3 = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, gameState.getUnitID(), Unit.class);
		// Tile initial3 = gameBoard.getTile(7,4);
		// avatar3.setPositionByTile(initial3);
		// avatar3.setPlayer(2);
		// avatar3.setAttack(2);
		// avatar3.setHealth(20);
		// avatar3.resetAction();
		// gameState.addAIUnit(avatar3);
		
		// BasicCommands.drawUnit(out, avatar3, initial3);
		// try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		// BasicCommands.setUnitAttack(out, avatar3, avatar3.getAttack());
		// try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		// BasicCommands.setUnitHealth(out, avatar3, avatar3.getHealth());
		// try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		// //this avatar is for testing
		
		// // this avatar is for testing
		// Unit avatar4 = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, gameState.getUnitID(), Unit.class);
		// Tile initial4 = gameBoard.getTile(3,4);
		// avatar4.setPositionByTile(initial4);
		// avatar4.setPlayer(2);
		// avatar4.setAttack(2);
		// avatar4.setHealth(20);
		// avatar4.resetAction();
		// gameState.addAIUnit(avatar4);
		
		// BasicCommands.drawUnit(out, avatar4, initial4);
		// try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		// BasicCommands.setUnitAttack(out, avatar4, avatar4.getAttack());
		// try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		// BasicCommands.setUnitHealth(out, avatar4, avatar4.getHealth());
		// try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		//this avatar is for testing
		
		
		// setPlayer1Health&mana
		gameState.setHumanMana(gameState.getTurnNum() + 1);
		BasicCommands.setPlayer1Health(out, gameState.getPlayer());
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setPlayer1Mana(out, gameState.getPlayer());

		// setPlayer2Health&mana
		BasicCommands.setPlayer2Health(out, gameState.getAi());
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setPlayer2Mana(out, gameState.getAi());

		//draw three cards from player deck to form starting hand
		for (int i = 0; i < 3; i++) {
			gameState.playerDrawCard();
		}
		gameState.displayHand(out);
		
		// draw three cards from AI deck as well
		for (int i = 0; i < 3; i++) {
			gameState.AIDrawCard();
		}

		
		// User 1 makes a change
		// this executes the command demo, comment out this when implementing your solution
		//CommandDemo.executeDemo(out); 
		//CheckMoveLogic.executeDemo(out);

	}

}


