package structures.basic;
import structures.GameState;
import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import structures.GameState;
public class Board {
	public void showBoard(ActorRef out, GameState gameState){
		for(int i=0;i<=8;i++) {
			for(int n=0;n<=4;n++) {
				Tile tile = BasicObjectBuilders.loadTile(i, n);
				BasicCommands.drawTile(out, tile, 0);
				String name = gameState.loadhumanBoard(i,n);
				if(name !=null) {
					Unit unit = BasicObjectBuilders.loadUnit(name, 0, Unit.class);
					unit.setPositionByTile(tile); 
					BasicCommands.drawUnit(out, unit, tile);
					try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
					BasicCommands.setUnitAttack(out, unit, 2);
					BasicCommands.setUnitHealth(out, unit, 2);
				}
				try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}
}
