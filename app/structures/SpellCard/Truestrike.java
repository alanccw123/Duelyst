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

public class Truestrike extends Card{

    @Override
    public List<Tile> checkTargets(GameState gameState) {
        List<Tile> targets = new ArrayList<>();

        //  truestrike targets all AI units
        for (Unit unit : gameState.getAIUnits()) {
            targets.add(unit.getTile());
        }

        return targets;
    }

    @Override
    public void playCard(ActorRef out, GameState gameState, Tile target) {
        Unit selected = target.getUnit();
        

        BasicCommands.addPlayer1Notification(out, "Truestrike", 1);
        
        // play effect animation
        EffectAnimation inmolation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
        BasicCommands.playEffectAnimation(out, inmolation, target);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gameState.unitTakeDamage(selected, out, 2);
        
        // decrease mana
        gameState.getPlayer().setMana(gameState.getPlayer().getMana() - manacost);
        BasicCommands.setPlayer1Mana(out, gameState.getPlayer());
            
        
    }  
}
