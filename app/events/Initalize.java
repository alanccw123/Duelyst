package events;

import com.fasterxml.jackson.databind.JsonNode;


import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import structures.Board;
import structures.GameState;
import structures.basic.AIAvatar;
import structures.basic.Player;
import structures.basic.PlayerAvatar;
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

		// Start the human turn
		gameState.changeTurn();

		gameState.gameInitalised = true;
		
		Board gameBoard = gameState.getGameBoard();
		
		// render the board on the frontend
		gameBoard.initialize(out);
		
		// place player's avatar
		PlayerAvatar avatar1 = (PlayerAvatar) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 99, PlayerAvatar.class);
		avatar1.setOwner(gameState.getPlayer());
		avatar1.setActorRef(out);
		Tile initial1 = gameBoard.getTile(1,2);
		avatar1.setPositionByTile(initial1);
		avatar1.setPlayer(1);
		avatar1.setAttack(2);
		avatar1.setMaxHealth(20);
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
		AIAvatar avatar2 = (AIAvatar) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 100, AIAvatar.class);
		avatar2.setOwner(gameState.getAi());
		avatar2.setActorRef(out);
		Tile initial2 = gameBoard.getTile(7,2);
		avatar2.setPositionByTile(initial2);
		avatar2.setPlayer(2);
		avatar2.setAttack(2);
		avatar2.setMaxHealth(20);
		avatar2.setHealth(20);
		avatar2.resetAction();
		gameState.addAIUnit(avatar2);
		
		BasicCommands.drawUnit(out, avatar2, initial2);
		try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, avatar2, avatar2.getAttack());
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, avatar2, avatar2.getHealth());
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		
		
		
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
			gameState.playerDrawCard(out);
		}
		gameState.displayHand(out);
		
		// draw three cards from AI deck as well
		for (int i = 0; i < 3; i++) {
			gameState.AIDrawCard(out);
		}

		
		// User 1 makes a change
		// this executes the command demo, comment out this when implementing your solution
		//CommandDemo.executeDemo(out); 
		//CheckMoveLogic.executeDemo(out);

	}

}


