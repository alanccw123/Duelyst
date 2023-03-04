package structures.basic;
import structures.basic.*;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class cardAzureHerald extends Card{
	int manacost = 2;
	
	public void UseCard(ActorRef out, GameState gameState, Tile tile) {
		
		BasicCommands.addPlayer1Notification(out, "You summoned Azure Herald on tile [ "+(tile.getTilex()+1) + ", " + (tile.getTiley()+1) + " ].", 1);
	    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//add 3 health to humanPlayer and human avatar, max to 20
	    if(gameState.getPlayer().getHealth() <= 17){
	    	gameState.getPlayer().setHealth(gameState.getPlayer().getHealth()+3);
            BasicCommands.setPlayer1Health(out, gameState.getPlayer());
            try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

            gameState.getPlayerUnits().get(0).setHealth(gameState.getPlayer().getHealth());
            BasicCommands.setUnitHealth(out, gameState.getPlayerUnits().get(0), gameState.getPlayer().getHealth());
            try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

	    }else {
	    	gameState.getPlayer().setHealth(20);
            BasicCommands.setPlayer1Health(out, gameState.getPlayer());
            try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

            gameState.getPlayerUnits().get(0).setHealth(20);
            BasicCommands.setUnitHealth(out, gameState.getPlayerUnits().get(0), 20);
            try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
	    }
		
		// summon the unit
        EffectAnimation summon = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
        BasicCommands.playEffectAnimation(out, summon, tile);
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

		gameState.addPlayerUnit(BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, gameState.getUnitID(), unitAzureHerald.class));
        gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1).setPositionByTile(tile);
        BasicCommands.drawUnit(out, gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1), tile);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

        gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1).setAttack(1);
        gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1).setHealth(4);
        BasicCommands.setUnitAttack(out, gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1), 1);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.setUnitHealth(out, gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1), 4);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1).setTile(tile);
        tile.setUnit(gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1));
        gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1).setOwner("human");
        gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1).setName("Azure Herald");
        gameState.setNewUnitID(gameState.getUnitID()+1);

        EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
        BasicCommands.playEffectAnimation(out, buff, gameState.getPlayerUnits().get(0).getTile());
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        
        //the first turn that the unit summoned, it cannot move or attack
        gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1).setAttacked(true);
        gameState.getPlayerUnits().get(gameState.getPlayerUnits().size()-1).setMoved(true);
        
        // minus player mana
        gameState.getPlayer().setMana(gameState.getPlayer().getMana() - manacost);
        BasicCommands.setPlayer1Mana(out, gameState.getPlayer());
        try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
        
        //remove card
//        gameState.removePlayerCard(23);
        gameState.setCurrentCard(null);
        for (int i = 0; i < gameState.getHumanHand().size(); i++){
            BasicCommands.deleteCard(out, i+ 1);
            try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
        }
        gameState.getHumanHand().remove(this);

        //redraw hand cards
        int j = 1;
        for (Card tmpCard : gameState.getHumanHand()){
            BasicCommands.drawCard(out, tmpCard, j++,0); //show the card on the screen's humanHand part
            try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
        }
        
        // cancel all tile highlights
        for (int i = 0; i < 9; i++){
            for (int k = 0; k < 5; k++){
                BasicCommands.drawTile(out, gameState.getGameBoard().getTile(i,k), 0);
                try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
            }
        }
        return;
    }
}
