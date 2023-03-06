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

public class AttackTest {

    @Test
    public void checkAttackRange() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board();
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

        List<Tile> targets = AttackChecker.checkAttackRange(start, board, unit.getPlayer());
        List<Tile> expected = List.of(tile3); // should only have the enemy unit within one tile range in the list

        assertTrue(targets.size() == expected.size() && targets.containsAll(expected) && expected.containsAll(targets));
    }

    @Test
    public void checkMoveAndAttack() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board();
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

        List<Tile> targets = AttackChecker.checkAllAttackRange(MovementChecker.checkMovement(start, board), board, unit.getPlayer());
        targets = targets.stream().distinct().collect(Collectors.toList());
        List<Tile> expected = List.of(tile3, tile4); // should have both enemy units within range in the list

        assertTrue(targets.size() == expected.size() && targets.containsAll(expected) && expected.containsAll(targets));
    }

    @Test
    public void checkAttackCalculation() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        GameState gameState = new GameState();
        Board board = gameState.getGameBoard();

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

        
        gameState.attack(unit, unit2, null);

        assertTrue(unit.getHealth() == 5 && unit2.getHealth() == 7);

    }

    @Test
    public void checkProvoke() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board();
        board.initialize(null);

        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Tile start = board.getTile(1, 2);
        unit.setPlayer(1);
        unit.setPositionByTile(start);

        Unit provoke = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 1, Unit.class);
        Tile tile2 = board.getTile(1, 1);
        provoke.setprovoke(true);
        provoke.setPlayer(2);
        provoke.setPositionByTile(tile2);

        Unit unit3 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_serpenti, 2, Unit.class);
        Tile tile3 = board.getTile(2, 1);
        unit3.setPlayer(2);
        unit3.setPositionByTile(tile3);

        List<Tile> targets = AttackChecker.checkAttackRange(start, board, unit.getPlayer());

        assertTrue(targets.size() == 1 & targets.contains(tile2));
    }
}
