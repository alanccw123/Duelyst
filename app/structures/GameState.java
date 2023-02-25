package structures;
import java.util.ArrayList;
import java.util.List;
import structures.basic.Player;
import utils.OrderedCardLoader;
import structures.basic.Card;
/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean humanTurn = false;
	
	public boolean something = false;

	private int humanStep;
	private int aiStep;
	public Player player;
	public Player ai;


	public void initalize(){
	}

	
	
	public int getHumanHealth() {
		return player.getHealth();
	}
	public void setHumanHealth(int a) {
		if(a > 20) {
			player.setHealth(20);
		}else {
			player.setHealth(a);
		}
	}
	public int getHumanMana() {
		return player.getMana();
	}
	public void setHumanMana(int mana) {
		if(mana >9) {
			player.setMana(9);
		}else {
			player.setMana(mana);
		}
	}
	
	
	public int getAiHealth() {
		return ai.getHealth();
	}
	public void setAiHealth(int a) {
		if(a > 20) {
			ai.setHealth(20);
		}else {
			ai.setHealth(a);
		}
	}
	public int getAiMana() {
		return ai.getMana();
	}
	public void setAiMana(int mana) {
		if(mana >9) {
			ai.setMana(9);
		}else {
			ai.setMana(mana);
		}
	}
	
	
	
	
	public void addHumanStep(int step) {
		humanStep = humanStep + step; 
	}
	public void addAiStep(int step) {
		humanStep =+ step; 
	}
	
	
	public int getHumanStep() {
		return humanStep;
	}
	public int getAiStep() {
		return aiStep;
	}
	
	
	public void setHumanStep(int n) {
		this.humanStep = n;
	}
	public void setAiStep(int n) {
		this.aiStep = n;
	}
}
