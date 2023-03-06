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

// This class contains several tests about calculation of tiles related to unit movement 
public class MovementTest {


    // This test checks whether the checkmovement method return a correct list of tiles that an unit on position (1,2) can move to
    @Test
    public void checkBasicMovement() {

        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board(); // create and inititialize a dummy board
        board.initialize(null);

        // place the test unit on tile(1,2)
        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Tile start = board.getTile(1, 2);
        unit.setPlayer(1);
        unit.setPositionByTile(start);

        // the list of expected tiles according to the game rules
        List<Tile> expected = List.of(board.getTile(1,0), board.getTile(0,1), board.getTile(1,1), board.getTile(2,1), 
        board.getTile(0,2), board.getTile(2,2), board.getTile(3,2), board.getTile(0,3), board.getTile(1,3), board.getTile(2,3),
        board.getTile(1,4));

        // get the list from checkmovement method
        List<Tile> range = MovementChecker.checkMovement(start, board);

        // check if the two lists are equal (disregarding order)
        assertTrue(range.size() == expected.size() && range.containsAll(expected) && expected.containsAll(range));
    }


    // This test checks whether the checkmovement method return a correct list of tiles when a provoke unit is present
    @Test
    public void checkProvoke() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board();// create and inititialize a dummy board
        board.initialize(null);

        // place the test unit on tile(1,2)
        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Tile start = board.getTile(1, 2);
        unit.setPlayer(1);
        unit.setPositionByTile(start);

        // place the provoke unit in adjacent to the test ujit
        Unit provoke = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 1, Unit.class);
        Tile tile2 = board.getTile(1, 1);
        provoke.setprovoke(true);
        provoke.setPlayer(2);
        provoke.setPositionByTile(tile2);

        // get the list of tile from checkmovement method
        List<Tile> range = MovementChecker.checkMovement(start, board);

        // the list should be empty because the unit is under effect of provoke and cannot move
        assertTrue(range.isEmpty());
    }

    
    // This test checks wether the checkmovement method return a correct list of tiles for a flying unit
    @Test
    public void checkFlying() {
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
		BasicCommands.altTell = altTell;

        Board board = new Board();// create and inititialize a dummy board
        board.initialize(null);

        // place the test unit on tile(1,2) with flying
        Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Tile start = board.getTile(1, 2);
        unit.setPlayer(1);
        unit.setPositionByTile(start);
        unit.setFlying(true);

        // place a unit on board
        Unit unit2 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 1, Unit.class);
        Tile tile2 = board.getTile(4, 3);
        unit2.setprovoke(true);
        unit2.setPlayer(2);
        unit2.setPositionByTile(tile2);

        // place a unit on board
        Unit unit3 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 2, Unit.class);
        Tile tile3 = board.getTile(8, 1);
        unit3.setprovoke(true);
        unit3.setPlayer(2);
        unit3.setPositionByTile(tile3);

        // place a unit on board
        Unit unit4 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 3, Unit.class);
        Tile tile4 = board.getTile(7, 2);
        unit4.setprovoke(true);
        unit4.setPlayer(2);
        unit4.setPositionByTile(tile4);

        // get the list from checkmovement method
        List<Tile> range = MovementChecker.checkMovement(start, board);

        //the list should contain all tiles of the board since the test unit is flying, except the three tiles occupied by other units
        assertFalse(range.contains(tile2) || range.contains(tile3) || range.contains(tile4));
    }
}
