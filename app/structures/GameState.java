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
	private int humanHealth;
	private int humanMana;
	private int aiHealth;
	private int aiMana;
	public int humanStep;
	public int aiStep;
	public int cardId = 3;
	public Player humanPlayer = new Player(20,0);
	public Player aiPlayer = new Player(20,0);
	private String humanBoard[][];
	private String aiBoard[][];
	private Card handCard[];
	public List<Card> player1Cards = OrderedCardLoader.getPlayer1Cards();
	public void initalize(){
		humanBoard = new String[9][5];
		aiBoard = new String[9][5];
		handCard = new Card[6];
	}
	public void sethumanBoard(int x,int y,String configFile) {
		humanBoard[x][y] = configFile;
	}
	public String loadhumanBoard(int x,int y) {
		return humanBoard[x][y];
	}
	
	
	public void setHandCard(int x,int id) {
		handCard[x]= player1Cards.get(id);
	}
	public Card getHandCard(int x) {
		return handCard[x];
	}
	
	
	
	
	
	public int getHumanHealth() {
		return humanHealth;
	}
	public void setHumanHealth(int a) {
		this.humanHealth = a;
	}
	public int getHumanMana() {
		return humanPlayer.getMana();
	}
	public void setHumanMana(int mana) {
		if(mana >9) {
			this.humanPlayer.setMana(9);
		}else {
			this.humanPlayer.setMana(mana);
		}
	}
	
	
	public int getAiHealth() {
		return aiHealth;
	}
	public void setAiHealth(int a) {
		this.aiHealth = a;
	}
	public int getAiMana() {
		return aiPlayer.getMana();
	}
	public void setAiMana(int mana) {
		if(mana >9) {
			this.aiPlayer.setMana(9);
		}else {
			this.aiPlayer.setMana(mana);
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
