package structures.basic;
import structures.basic.*;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class cardWindshrike extends Card{
    int manacost=4;
    public void UseCard(ActorRef out, GameState gameState, Tile tile){
        BasicCommands.addPlayer1Notification(out, "Enemy summoned WindShrink on tile " + "["+(tile.getTilex()+1) + ", " + (tile.getTiley()+1) + " ].", 3);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        EffectAnimation summon = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
        BasicCommands.playEffectAnimation(out, summon, tile);
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.addAIUnit(BasicObjectBuilders.loadUnit(StaticConfFiles.u_windshrike, gameState.getUnitID(), unitWindshrike.class));
        gameState.getAIUnits().get(gameState.getAIUnits().size()-1).setPositionByTile(tile);
        BasicCommands.drawUnit(out, gameState.getAIUnits().get(gameState.getAIUnits().size()-1), tile);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.getAIUnits().get(gameState.getAIUnits().size()-1).setAttack(4);
        gameState.getAIUnits().get(gameState.getAIUnits().size()-1).setHealth(3);
        BasicCommands.setUnitAttack(out, gameState.getAIUnits().get(gameState.getAIUnits().size()-1), 4);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.setUnitHealth(out, gameState.getAIUnits().get(gameState.getAIUnits().size()-1), 3);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.getAIUnits().get(gameState.getAIUnits().size()-1).setTile(tile);
        tile.setUnit(gameState.getAIUnits().get(gameState.getAIUnits().size()-1));
        gameState.getAIUnits().get(gameState.getAIUnits().size()-1).setOwner("bot");
        gameState.getAIUnits().get(gameState.getAIUnits().size()-1).setName("Windshrike");
        gameState.setNewUnitID(gameState.getNewUnitID()+1);
        //the first turn that the unit summoned, it cannot move or attack
        gameState.getAIUnits().get(gameState.getAIUnits().size()-1).setAttacked(true);
        gameState.getAIUnits().get(gameState.getAIUnits().size()-1).setMoved(true);
        // minus player mana
        gameState.getAi().setMana(gameState.getAi().getMana() - this.manacost);
        BasicCommands.setPlayer2Mana(out, gameState.getAi());
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //remove card
        gameState.setCurrentCard(null);
        gameState.getBotHand().remove(this);
        return;
    }
}
