package structures.basic;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

public class unitWindshrike extends Unit{
    //move anywhere on the board
    public boolean tileInMoveRange(Tile tile, GameState gameState) {
        if (moved || attacked) return false;
        if (tile.getUnit() != null) return false;
        else return true;
    }
    //When this unit dies,its owner draws a card
    public void dieUnit(ActorRef out, GameState gameState) {
        BasicCommands.playUnitAnimation(out, this, UnitAnimationType.death);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BasicCommands.deleteUnit(out, this);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.getTile().setUnit(null);
        if (this.getOwner() == "bot") gameState.getBotUnits().remove(this);
        else gameState.getHumanUnits().remove(this);
        if (!gameState.getBotDeck().isEmpty()){
            gameState.addBotHand(gameState.getBotDeck().get(0)); // add the first card to botHand
            gameState.getBotDeck().remove(0); //remove the first card from botDeck
        }
    }

}
