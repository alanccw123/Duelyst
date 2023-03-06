import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;
import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import play.libs.Json;
import play.mvc.BodyParser.Of;
import structures.Board;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.AttackChecker;
import utils.BasicObjectBuilders;
import utils.MovementChecker;
import utils.StaticConfFiles;


// This class contains tests about calculation related to attack
public class AttackTest {

    // This test checks wether the checkAttackRange method return a correct list of tiles for attack, without considering move-and-attack 
    @Test
    public void checkAttackRange() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board(); // create and inititialize a dummy board
        board.initialize(null);

        // unit for testing
        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Tile start = board.getTile(1, 2);
        unit.setPlayer(1);
        unit.setPositionByTile(start);

        // add a friendly unit 
        Unit unit2 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 1, Unit.class);
        Tile tile2 = board.getTile(1, 3);
        unit2.setprovoke(true);
        unit2.setPlayer(1);
        unit2.setPositionByTile(tile2);

        // add an enemy unit within one tile range
        Unit unit3 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_serpenti, 2, Unit.class);
        Tile tile3 = board.getTile(2, 2);
        unit3.setPlayer(2);
        unit3.setPositionByTile(tile3);

        // add an enemy unit outside one tile range
        Unit unit4 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_pyromancer, 3, Unit.class);
        Tile tile4 = board.getTile(4, 2);
        unit4.setPlayer(2);
        unit4.setPositionByTile(tile4);

        
        // the expected list should contain only the tile with enemy unit within one tile range
        // because the friendly unit is unattackable and the enemy unti on tile(4,2) is outside
        // basic attack range 
        List<Tile> expected = List.of(tile3); 

        List<Tile> targets = AttackChecker.checkAttackRange(start, board, unit.getPlayer()); // get the list from checkAttackRange method


        // check if the two lists are equal (disregarding order)
        assertTrue(targets.size() == expected.size() && targets.containsAll(expected) && expected.containsAll(targets));
    }

    // This test checks wether the checkAllAttackRange method correctly consider the tiles for attack, including those for move-and-attack
    @Test
    public void checkMoveAndAttack() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board(); // create and inititialize a dummy board
        board.initialize(null);

        // unit for testing
        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Tile start = board.getTile(1, 2);
        unit.setPlayer(1);
        unit.setPositionByTile(start);

        // add a friendly unit 
        Unit unit2 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 1, Unit.class);
        Tile tile2 = board.getTile(1, 3);
        unit2.setprovoke(true);
        unit2.setPlayer(1);
        unit2.setPositionByTile(tile2);

        // add an enemy unit within one tile range
        Unit unit3 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_serpenti, 2, Unit.class);
        Tile tile3 = board.getTile(2, 1);
        unit3.setPlayer(2);
        unit3.setPositionByTile(tile3);

        // add an enemy unit within range after moving
        Unit unit4 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_pyromancer, 3, Unit.class);
        Tile tile4 = board.getTile(4, 2);
        unit4.setPlayer(2);
        unit4.setPositionByTile(tile4);

        // get the list from checkAttackRange method
        List<Tile> targets = AttackChecker.checkAllAttackRange(MovementChecker.checkMovement(start, board), board, unit.getPlayer());

        // remove the dupilcate tiles from the list before comparing
        // note that dupilcate tiles do not effect the correctness of the game
        // when rendering highlighted tiles on the frontend
        targets = targets.stream().distinct().collect(Collectors.toList());

        // the expected list should contain both tiles with enemy units 
        // because one is already in range and the other on tile(4,2) is in range 
        // for a move-and-attack 
        List<Tile> expected = List.of(tile3, tile4); 

        // check if the two lists are equal (disregarding order)
        assertTrue(targets.size() == expected.size() && targets.containsAll(expected) && expected.containsAll(targets));
    }

    // This test checks whether the GameState.attack method process an attack correcly
    @Test
    public void checkAttackCalculation() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        GameState gameState = new GameState(); // create and inititialize a GameState object
        Board board = gameState.getGameBoard(); // get the board from storage

        // unit for testing with 3 attack and 10 health
        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Tile start = board.getTile(1, 2);
        unit.setPlayer(1);
        unit.setPositionByTile(start);
        unit.setMaxHealth(10);
        unit.setHealth(10);
        unit.setAttack(3);
        unit.resetAction();

        // enemy unit with 5 attack and 10 health
        Unit unit2 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_serpenti, 2, Unit.class);
        Tile tile2 = board.getTile(2, 1);
        unit2.setPlayer(2);
        unit2.setPositionByTile(tile2);
        unit2.setMaxHealth(10);
        unit2.setHealth(10);
        unit2.setAttack(5);
        unit2.resetAction();

        // call the attack method to process an attack
        gameState.attack(unit, unit2, null);

        // After the attack, the defender should have 7 health remaining
        // and because it survives the can counter-attack, the attacker
        // should have 5 health remaining
        assertTrue(unit.getHealth() == 5 && unit2.getHealth() == 7);

    }

    // This test checks wether the checkAttack method correcly return a list of tiles for attack in presence of provoke unit
    @Test
    public void checkProvoke() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board(); // create and inititialize a dummy board
        board.initialize(null);

        // unit for testing
        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Tile start = board.getTile(1, 2);
        unit.setPlayer(1);
        unit.setPositionByTile(start);

        // enemy provoke unit
        Unit provoke = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 1, Unit.class);
        Tile tile2 = board.getTile(1, 1);
        provoke.setprovoke(true);
        provoke.setPlayer(2);
        provoke.setPositionByTile(tile2);

        // enemy unit without provoke
        Unit unit3 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_serpenti, 2, Unit.class);
        Tile tile3 = board.getTile(2, 1);
        unit3.setPlayer(2);
        unit3.setPositionByTile(tile3);

        List<Tile> targets = AttackChecker.checkAttackRange(start, board, unit.getPlayer());

        // the list should contain only the tile with provoke unit as it it the only valid target in this case
        assertTrue(targets.size() == 1 & targets.contains(tile2));
    }
}
