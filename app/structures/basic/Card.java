package structures.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.MovementChecker;
import utils.StaticConfFiles;

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

	// check for targets to play the card on
	// returns a list of tiles
	// base version is for summoning units, overidden in child classes to implement spell cards
	public List<Tile> checkTargets(GameState gameState, int player) {
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

	// execute the effect of the card
	// base version is for summoning units, overridden in child classes to implement spell cards
	public void playCard(ActorRef out, GameState gameState, Tile target) {
		
		
		
		
		
		// create the corresponding unit
		Unit unit = BasicObjectBuilders.loadUnit(mapping.get(cardname), id, Unit.class);
		unit.setPositionByTile(target);

		// decrement mana 
		if (gameState.isPlayerTurn()) {
			unit.setPlayer(1);
			gameState.addPlayerUnit(unit);
			gameState.setHumanMana(gameState.getHumanMana() - manacost);
			BasicCommands.setPlayer1Mana(out, gameState.getPlayer());
		}else {
			unit.setPlayer(2);
			gameState.addAIUnit(unit);
			gameState.setAiMana(gameState.getAiMana() - manacost);
			BasicCommands.setPlayer2Mana(out, gameState.getAi());
		}

		// set unit's attack and health
		unit.setAttack(bigCard.getAttack());
		unit.setHealth(bigCard.getHealth());

		// render the unit on the frontend
		EffectAnimation summon = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, summon, target);
		BasicCommands.drawUnit(out, unit, target);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BasicCommands.setUnitAttack(out, unit, unit.getAttack());
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BasicCommands.setUnitHealth(out, unit, unit.getHealth());
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		
		
	}

	// hashmap to find the approriate config files
	private static final Map<String, String> mapping = new HashMap<>();
    static {
        mapping.put("Comodo Charger", StaticConfFiles.u_comodo_charger);
        mapping.put("Hailstone Golem", StaticConfFiles.u_hailstone_golem);
		mapping.put("Azure Herald", StaticConfFiles.u_azure_herald);
		mapping.put("Azurite Lion", StaticConfFiles.u_azurite_lion);
		mapping.put("Pureblade Enforcer", StaticConfFiles.u_pureblade_enforcer);
		mapping.put("Ironcliff Guardian", StaticConfFiles.u_ironcliff_guardian);
		mapping.put("Silverguard Knight", StaticConfFiles.u_silverguard_knight);
		mapping.put("Fire Spitter", StaticConfFiles.u_fire_spitter);
		mapping.put("Blaze Hound", StaticConfFiles.u_blaze_hound);
		mapping.put("Bloodshard Golem", StaticConfFiles.u_bloodshard_golem);
		// mapping.put("Hailstone Golem", StaticConfFiles.u_hailstone_golemR);
		mapping.put("Planar Scout", StaticConfFiles.u_planar_scout);
		mapping.put("Pyromancer", StaticConfFiles.u_pyromancer);
		mapping.put("Rock Pulveriser", StaticConfFiles.u_rock_pulveriser);
		mapping.put("Serpenti", StaticConfFiles.u_serpenti);
		mapping.put("WindShrike", StaticConfFiles.u_windshrike);
		
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
