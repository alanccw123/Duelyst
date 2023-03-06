import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

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
import utils.BasicObjectBuilders;
import utils.MovementChecker;
import utils.StaticConfFiles;


public class MovementTest {

    @Test
    public void checkBasicMovement() {

        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board();
        board.initialize(null);

        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Tile start = board.getTile(1, 2);
        unit.setPlayer(1);
        unit.setPositionByTile(start);

        List<Tile> expected = List.of(board.getTile(1,0), board.getTile(0,1), board.getTile(1,1), board.getTile(2,1), 
        board.getTile(0,2), board.getTile(2,2), board.getTile(3,2), board.getTile(0,3), board.getTile(1,3), board.getTile(2,3),
        board.getTile(1,4));
        List<Tile> range = MovementChecker.checkMovement(start, board);

        assertTrue(range.size() == expected.size() && range.containsAll(expected) && expected.containsAll(range));
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

        List<Tile> range = MovementChecker.checkMovement(start, board);

        assertTrue(range.isEmpty());
    }

    @Test
    public void checkFlying() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board();
        board.initialize(null);

        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Tile start = board.getTile(1, 2);
        unit.setPlayer(1);
        unit.setPositionByTile(start);
        unit.setFlying(true);

        Unit unit2 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 1, Unit.class);
        Tile tile2 = board.getTile(4, 3);
        unit2.setprovoke(true);
        unit2.setPlayer(2);
        unit2.setPositionByTile(tile2);

        Unit unit3 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 2, Unit.class);
        Tile tile3 = board.getTile(8, 1);
        unit3.setprovoke(true);
        unit3.setPlayer(2);
        unit3.setPositionByTile(tile3);

        Unit unit4 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 3, Unit.class);
        Tile tile4 = board.getTile(7, 2);
        unit4.setprovoke(true);
        unit4.setPlayer(2);
        unit4.setPositionByTile(tile4);

        List<Tile> range = MovementChecker.checkMovement(start, board);

        assertFalse(range.contains(tile2) || range.contains(tile3) || range.contains(tile4));
    }
}
