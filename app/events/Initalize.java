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
		
		gameState.gameInitalised = true;
		
		Board gameBoard = new Board();
		
		gameState.gameBoard = gameBoard;
		
		gameBoard.initialize(out);
		
		Unit avatar1 = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
		Tile initial1 = gameBoard.getTile(1,2);
		avatar1.setPositionByTile(initial1);
		initial1.setUnit(avatar1);
		

		Unit avatar2 = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);
		Tile initial2 = gameBoard.getTile(7,2);
		avatar2.setPositionByTile(initial2);
		initial2.setUnit(avatar2);
		
		
		// this avatar is for testing
		Unit avatar3 = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 2, Unit.class);
		Tile initial3 = gameBoard.getTile(7,4);
		avatar3.setPositionByTile(initial3);
		initial3.setUnit(avatar3);
		BasicCommands.drawUnit(out, avatar3, initial3);
		try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		//this avatar is for testing
		
		
		BasicCommands.drawUnit(out, avatar1, initial1);
		try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.setUnitAttack(out, avatar1, 2);
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, avatar1, 20);
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		
		
		BasicCommands.drawUnit(out, avatar2, initial2);
		try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.setUnitAttack(out, avatar2, 2);
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, avatar2, 20);
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		
		
		// setPlayer1Health
		Player humanPlayer = new Player(20, 0);
		BasicCommands.setPlayer1Health(out, humanPlayer);
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}

		// setPlayer2Health
		Player aiPlayer = new Player(20, 0);
		BasicCommands.setPlayer2Health(out, aiPlayer);
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}

		
		
		// User 1 makes a change
		// this executes the command demo, comment out this when implementing your solution
//		CommandDemo.executeDemo(out); 
		//CheckMoveLogic.executeDemo(out);
	}

}


