package structures.basic;

import java.util.ArrayList;
import java.util.Arrays;
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
	protected int manacost;
	
	MiniCard miniCard;
	BigCard bigCard;

	// check for targets to play the card on
	// returns a list of tiles
	// base version is for summoning units, overidden in child classes to implement spell cards
	public List<Tile> checkTargets(GameState gameState, int player) {
		List<Unit> friendlyunits = new ArrayList<>();
		List<Tile> targets = new ArrayList<>();
		Board board = gameState.getGameBoard();

		// check if the card is air-drop unit
		Integer[] airDropUnits = new Integer[]{6, 16, 28, 38};
		if (Arrays.stream(airDropUnits).anyMatch(x -> x == id)) {
			// if so, all tiles un-occupied are valid
			for (int i = 0; i < board.board.length; i++) {
				for (int j = 0; j < board.board[i].length; j++) {
					if (!board.getTile(i, j).hasUnit) {
						targets.add(board.getTile(i, j));
					}
				}
			}

			return targets;
		}

		// get list of friendly units based on whose turn it is
		if (player == 1) {
			friendlyunits = gameState.getPlayerUnits();
		}else if (player == 2) {
			friendlyunits = gameState.getAIUnits();
		}

		// check adjacent tiles of all friendy units for unoccupied tiles
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
		
			BasicCommands.addPlayer1Notification(out, String.format("Play card id: %d", id), 1);
		
		// if it is a spell card do nothing
		if (mapping.get(cardname) == null) {
			return;
		}

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
		unit.setMaxHealth(bigCard.getHealth());
		unit.setHealth(bigCard.getHealth());
		

		// card id matching with provoke units
		Integer[] provokeUnits = new Integer[]{3, 6, 10, 16, 20, 30};
		if (Arrays.stream(provokeUnits).anyMatch(x -> x == id)) {
			unit.setprovoke(true); // set the unit to provoke
		}

		// card id matching with ranged units
		Integer[] rangedUnits = new Integer[]{2, 11, 25, 35};
		if (Arrays.stream(rangedUnits).anyMatch(x -> x == id)) {
			unit.setRanged(true); // set the unit to ranged
		}

		// card id matching with double attack
		Integer[] doubleAttackUnits = new Integer[]{7, 17, 26, 36};
		if (Arrays.stream(doubleAttackUnits).anyMatch(x -> x == id)) {
			unit.setMaxAttackAction(2); // set the unit's max attack action to 2
		}
		
		// card id matching with flying units
		Integer[] flyingUnits = new Integer[]{24, 34};
		if (Arrays.stream(flyingUnits).anyMatch(x -> x == id)) {
			unit.setFlying(true); // set the unit to flying
		}

		// if summoning a blaze hound
		if (id == 23 || id == 33) {
			//both players draw a card
			gameState.AIDrawCard(out);
			gameState.playerDrawCard(out);
			gameState.displayHand(out);
		}

		// if summoning a azure herald
		if (id == 5 || id == 15) {
			for (Unit friendly : gameState.getPlayerUnits()) {
				// increment avatar's HP by 3
				if (friendly.getId() == 99) {
					friendly.setHealth(friendly.getHealth() + 3);
					BasicCommands.setUnitHealth(out, friendly, friendly.getHealth());
				}
			}
		}

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
		
		BasicCommands.addPlayer1Notification(out, String.format("Summon unit id: %d", id), 1);
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
