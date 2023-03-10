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

public class SundropElixir extends Card{

    @Override
    public List<Tile> checkTargets(GameState gameState, int player) {
        List<Tile> targets = new ArrayList<>();

        for (Unit u : gameState.getPlayerUnits()){
            targets.add(u.getTile());
        }
        for (Unit u : gameState.getAIUnits()){
            targets.add(u.getTile());
        }

        return targets;
    }

    @Override
    public void playCard(ActorRef out, GameState gameState, Tile target) {
        BasicCommands.addPlayer1Notification(out, "Sundrop Elixir", 1);
        Unit selected = target.getUnit();
        selected.setHealth(selected.getHealth() + 5);

        EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
        BasicCommands.playEffectAnimation(out, buff, target);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.setUnitHealth(out, selected, selected.getHealth());

        gameState.getPlayer().setMana(gameState.getPlayer().getMana() - manacost);
		BasicCommands.setPlayer1Mana(out, gameState.getPlayer());
    }
    
}
