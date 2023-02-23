package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * <p>
 * {
 * messageType = “initalize”
 * }
 *
 * @author Dr. Richard McCreadie
 */
public class Initalize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        gameState.humanTurn = true;
        gameState.something = true;
        gameState.initalize();
        gameState.setHandCard(0, 0);
        gameState.setHandCard(1, 1);
        gameState.setHandCard(2, 2);
        Card No1 = gameState.getHandCard(0);
        Card No2 = gameState.getHandCard(1);
        Card No3 = gameState.getHandCard(2);
        BasicCommands.drawCard(out, No1, 1, 0);
        BasicCommands.drawCard(out, No2, 2, 0);
        BasicCommands.drawCard(out, No3, 3, 0);
    }




    //CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
    //CheckMoveLogic.executeDemo(out);





    }



