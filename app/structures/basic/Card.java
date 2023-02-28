package structures.basic;

import java.util.ArrayList;
import java.util.List;

import structures.Board;
import structures.GameState;
import utils.MovementChecker;

/**
 * This is the base representation of a Card which is rendered in the player's hand.
 * A card has an id, a name (cardname) and a manacost. A card then has a large and mini
 * version. The mini version is what is rendered at the bottom of the screen. The big
 * version is what is rendered when the player clicks on a card in their hand.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Card {
	
	int id;
	
	String cardname;
	int manacost;
	
	MiniCard miniCard;
	BigCard bigCard;

	public List<Tile> checkTargets(GameState gameState, int player) {
		//to-do
		List<Unit> friendlyunits = new ArrayList<>();
		List<Tile> targets = new ArrayList<>();
		Board board = gameState.getGameBoard();

		if (player == 1) {
			friendlyunits = gameState.getPlayerUnits();
		}else if (player == 2) {
			friendlyunits = gameState.getAIUnits();
		}

		for (Unit unit : friendlyunits) {
			Tile friendly = unit.getTile();
			int x = friendly.getTilex();
			int y = friendly.getTiley();
			
			for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
					if (MovementChecker.withinBoard(i, j)) {
						Tile tile = board.getTile(i, j);
						if (!tile.isHasUnit()) {
							targets.add(tile);
						}
					}
				}
			}
		}

		return targets;
		
	}

	public void playCard() {
		//to-do
	}
	
	public Card() {};
	
	public Card(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super();
		this.id = id;
		this.cardname = cardname;
		this.manacost = manacost;
		this.miniCard = miniCard;
		this.bigCard = bigCard;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCardname() {
		return cardname;
	}
	public void setCardname(String cardname) {
		this.cardname = cardname;
	}
	public int getManacost() {
		return manacost;
	}
	public void setManacost(int manacost) {
		this.manacost = manacost;
	}
	public MiniCard getMiniCard() {
		return miniCard;
	}
	public void setMiniCard(MiniCard miniCard) {
		this.miniCard = miniCard;
	}
	public BigCard getBigCard() {
		return bigCard;
	}
	public void setBigCard(BigCard bigCard) {
		this.bigCard = bigCard;
	}

	
}
