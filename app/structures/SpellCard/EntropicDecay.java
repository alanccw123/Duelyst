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

public class EntropicDecay extends Card{
    @Override
    public List<Tile> checkTargets(GameState gameState, int player) {
        List<Tile> targets = new ArrayList<>();

        for (Unit u : gameState.getPlayerUnits()){
            if(u.getId()!=99) {
                targets.add(u.getTile());
            }
        }

        for (Unit u : gameState.getAIUnits()){
            if(u.getId()!=100) {
                targets.add(u.getTile());
            }
        }

        return targets;
    }

    @Override
    public void playCard(ActorRef out, GameState gameState, Tile target) {
        Unit selected = target.getUnit();

        EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom);
        BasicCommands.playEffectAnimation(out, effect, target);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.setUnitHealth(out, selected, 0);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

        gameState.unitTakeDamage(selected, out, selected.getHealth());

        for (Unit u : gameState.getPlayerUnits()){
            if (u.getId()==1 || u.getId()==13){
                EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
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
