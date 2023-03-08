package events;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.Board;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.AttackChecker;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		/*// if it is player's turn, switch to AI's turn
	    if (gameState.isPlayerTurn()) {
	        // Player defaultMana = new Player(20, 0);
	        // gameState.humanTurn = false;
	        // gameState.something = false;
	        // gameState.setAiStep(0);
			gameState.changeTurn();

			// discard any unused mana
	        gameState.setHumanMana(0);
	        BasicCommands.setPlayer1Mana(out, gameState.getPlayer());

			// draw a card
	        if (gameState.playerDrawCard(out)) {
	            BasicCommands.addPlayer1Notification(out, "Draw a card", 2);
	        }else{
				BasicCommands.addPlayer1Notification(out, "Your hand is full!", 2);
			}
			gameState.displayHand(out);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// AI turn starts
			BasicCommands.addPlayer1Notification(out, "AI's Turn", 2);
			gameState.setAiMana(gameState.getTurnNum() + 1);
			BasicCommands.setPlayer2Mana(out, gameState.getAi());

			// reset units action
			gameState.resetAllAction();

			// AI runs on a sperate thread
	        AI opponent = new AI(out, gameState, message);
	        opponent.start();
	    }
	}

	class AI extends Thread {
	    ActorRef out;
	    GameState gameState;
	    JsonNode message;

	    public AI(ActorRef out, GameState gameState, JsonNode message) {
	        this.out = out;
	        this.gameState = gameState;
	        this.message = message;
	    }

	    public void run() {
			// code for AI goes in here!!!!
	        try {
	            Thread.sleep(5000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			
			// code for esting cards in AI deck
			Card found = null;
			for (Card card : gameState.getAIHand()) {
				if (card.getId() == 24 || card.getId() == 23) {
					found = card;
				}
			}
			if (found != null) {
				List<Tile> targets = found.checkTargets(gameState, 2);
				Random rand = new Random();
				Tile randomTile = targets.get(rand.nextInt(targets.size()));
				gameState.removeAICard(found);
				found.playCard(out, gameState, randomTile);
			}
			// code for testing cards in AI deck
			

	        // Player defaultMana = new Player(20, 0);
	        // gameState.something = true;
	        // gameState.setHumanStep(0);
			
			// discard unused mana
	        gameState.setAiMana(0);
	        BasicCommands.setPlayer2Mana(out, gameState.getAi());

			// draw a card
			gameState.AIDrawCard(out);

			// player gain mana
			gameState.incrementTurn();
			gameState.setHumanMana(gameState.getTurnNum() + 1);

			// Pass back to Player turn
	        BasicCommands.addPlayer1Notification(out, "Your Turn", 2);
			BasicCommands.setPlayer1Mana(out, gameState.getPlayer());
			gameState.resetAllAction();
			gameState.changeTurn();
	    }*/
		List<Unit> aiUnits = gameState.getAIUnits();//场上所有电脑单位
		List<Unit> play=gameState.getPlayerUnits();
		List<Card> aiHand = gameState.getAIHand();//电脑牌组
		Map<Integer, Integer> map = init_AiCard(aiHand);//赋值好的电脑牌组评分
//		Card card = Play_Card(map, aiHand);

		// search for entropic decay and play it
		Card entropicDecay = null;
		for (Card card :
				aiHand) {
			if (card.getCardname().equals("Entropic Decay")){
				entropicDecay = card;
			}
		}

		List<Tile> targets = entropicDecay.checkTargets();

		int maxHealth = 0;
		Tile target = null;
		for (Tile tile :
				targets) {
			Unit unit = tile.getUnit();
			if (unit.getPlayer() == 1 && unit.getHealth() > maxHealth){
				maxHealth = unit.getHealth();
				target = tile;
			}
		}

		entropicDecay.playCard(out, gameState, target);

		Card


		//如果为法术牌选定敌方血量最高单位做攻击
//		Unit unit=null;//攻击单位
//		if(card.getCardname().equals("Entropic Decay")){
//			int max=0;
//
//			for(int i=0;i<play.size();i++){
//				if(play.get(i).getHealth()>max){
//					unit=play.get(i);
//					max=play.get(i).getHealth();
//				}
//			}
//
//		}else if(card.getCardname().equals("Staff of Y'Kir")) {
//			int max=0;
//
//			for(int i=0;i<play.size();i++){
//				if(play.get(i).getHealth()>max){
//					unit=play.get(i);
//					max=play.get(i).getHealth();
//				}
//			}
//		}

		Map<Integer, Integer> map1 = init_PlayerCard(gameState.getPlayerHand());//玩家手牌的评分
		;//选定攻击单位
		gameState.attack(gameState.unitLastClicked,Attack_Play(map1,gameState),out);//攻击玩家
	}

	/**
	 *
	 * 给电脑牌组添加评分
	 * @param Ai_Cards
	 * @return
	 */
	public Map<Integer, Integer> init_AiCard(List<Card> Ai_Cards){
		Map<Integer,Integer> map=new HashMap<>();
		for(int i=0;i<Ai_Cards.size();i++){
			if(Ai_Cards.get(i).getCardname().equals("Planar Scout")){
				map.put(Ai_Cards.get(i).getId(),30);
			}else if(Ai_Cards.get(i).getCardname().equals("Rock Pulveriser")){
				map.put(Ai_Cards.get(i).getId(),54);
			}else if(Ai_Cards.get(i).getCardname().equals("Pyromancer")){
				map.put(Ai_Cards.get(i).getId(),65);
			}else if(Ai_Cards.get(i).getCardname().equals("Bloodshard Golem")){
				map.put(Ai_Cards.get(i).getId(),60);
			}else if(Ai_Cards.get(i).getCardname().equals("Blaze Hound")){
				map.put(Ai_Cards.get(i).getId(),61);
			}else if(Ai_Cards.get(i).getCardname().equals("Windshrike")){
				map.put(Ai_Cards.get(i).getId(),50);
			}else if(Ai_Cards.get(i).getCardname().equals("Hailstone Golem")){
				map.put(Ai_Cards.get(i).getId(),56);
			}else if(Ai_Cards.get(i).getCardname().equals("Serpenti")){
				map.put(Ai_Cards.get(i).getId(),65);
			}else if(Ai_Cards.get(i).getCardname().equals("botAllUnitsCardsScore")){
				map.put(Ai_Cards.get(i).getId(),80);
			}else if(Ai_Cards.get(i).getCardname().equals("Entropic Decay")){
				map.put(Ai_Cards.get(i).getId(),100);
			}
		}
		return map;
	}


	/***
	 *
	 * 获取Al评分最大的牌并返回
	 * @param integerMap
	 * @param cards
	 * @return
	 */
	public Card Play_Card(Map<Integer,Integer> integerMap,List<Card> cards){
		Card card=cards.get(0);//评分最大的牌
		for(int i=1;i<cards.size();i++){
			if(integerMap.get(card.getId())<integerMap.get(cards.get(i).getId())){
				card=cards.get(i);
			}
		}

		return card;
	}

	/**
	 *
	 * 召唤
	 * @return
	 */
	//算某一个格子对对方的化身差多远  用
	public Summon(GameState gameState){
		Board gameBoard = null;
		int min=100;
		for(int i=0;i<9;i++){
			for (int j=0;j<5;j++){
				if(Math.abs(i-gameState.get(99).getTile().getTilex())){
					gameBoard = gameState.getGameBoard();
				}
			}
		}
		return gameBoard;
	}

	/**
	 *
	 * 给玩家牌组添加评分
	 * @param P
	 * @return
	 */
	public Map<Integer, Integer> init_PlayerCard(List<Card> P){
		Map<Integer,Integer> map=new HashMap<>();
		for(int i=0;i<P.size();i++){
			if(P.get(i).getCardname().equals("Comodo Charger")){
				map.put(P.get(i).getId(),30);
			}else if(P.get(i).getCardname().equals("Hailstone Golem")){
				map.put(P.get(i).getId(),70);
			}else if(P.get(i).getCardname().equals("Pureblade Enforcer")){
				map.put(P.get(i).getId(),50);
			}else if(P.get(i).getCardname().equals("Azure Herald")){
				map.put(P.get(i).getId(),41);
			}else if(P.get(i).getCardname().equals("Silverguard Knight")){
				map.put(P.get(i).getId(),85);
			}else if(P.get(i).getCardname().equals("Azurite Lion")){
				map.put(P.get(i).getId(),40);
			}else if(P.get(i).getCardname().equals("Fire Spitter")){
				map.put(P.get(i).getId(),59);
			}else if(P.get(i).getCardname().equals("Ironclif Guardian")){
				map.put(P.get(i).getId(),89);
			}else if(P.get(i).getCardname().equals("Truestrike")){
				map.put(P.get(i).getId(),61);
			}else if(P.get(i).getCardname().equals("Sundrop Elixir")){
				map.put(P.get(i).getId(),91);
			}
		}
		return map;
	}


	/**
	 *
	 * 判断电脑攻击对象
	 * @param map
	 * @param gameState
	 * @return
	 */

	public Unit Attack_Play(Map<Integer, Integer> map,GameState gameState){
		List<Unit> playerUnits = gameState.getPlayerUnits();//玩家的单位
		Unit unit = null;
		boolean a=false;
		for(int i=0;i<playerUnits.size();i++){
			if(playerUnits.get(i).getId()==99){
				a=playerUnits.get(i).canAttack();//判断化身是否可以被攻击
				unit=playerUnits.get(i);
			}
		}
		if(a){
			return unit;
		}else {
			int a1=0;
			for(int i=0;i<playerUnits.size();i++){
				if(map.get(playerUnits.get(i).getId())>a1){
					a1=map.get(playerUnits.get(i).getId());
					unit=playerUnits.get(i);
				}
			}
		}

		return unit;
	}
}