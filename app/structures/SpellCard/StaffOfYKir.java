package structures.SpellCard;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class StaffOfYKir extends Card{

    @Override
    public List<Tile> checkTargets(GameState gameState, int player) {
        List<Tile> targets = new ArrayList<>();

        for (Unit unit : gameState.getAIUnits()) {
            if (unit.getId() == 100) {
                targets.add(unit.getTile());
            }
        }

        return targets;
    }

    @Override
    public void playCard(ActorRef out, GameState gameState, Tile target) {

        Unit selected = target.getUnit();

        EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
        BasicCommands.playEffectAnimation(out, buff, target);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}


        selected.setAttack(selected.getAttack()+2);
        BasicCommands.setUnitAttack(out, selected, selected.getAttack());
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

        for (Unit u : gameState.getPlayerUnits()){
            if (u.getId()==1 || u.getId()==13){
                BasicCommands.playEffectAnimation(out, buff, u.getTile());
                try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                u.setAttack(u.getAttack()+1); //Attack plus 1
                u.setMaxHealth(u.getMaxHealth() + 1);
                u.setHealth(u.getHealth()+1); //Health plus 1
                BasicCommands.setUnitAttack(out, u, u.getAttack());
                try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
                BasicCommands.setUnitHealth(out, u, u.getHealth());
                try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
            }
        }

        gameState.getAi().setMana(gameState.getAi().getMana() - manacost);
		BasicCommands.setPlayer2Mana(out, gameState.getAi());
    }
    
}
