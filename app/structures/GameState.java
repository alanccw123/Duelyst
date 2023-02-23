package structures;

import structures.basic.Card;
import utils.OrderedCardLoader;

import java.util.List;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 *
 * @author Dr. Richard McCreadie
 */
public class GameState {


    public boolean gameInitalised = false;

    public boolean something = false;

    public boolean humanTurn = false;

    private Card handCard[];
    public int cardId = 3;
    public List<Card> player1Cards = OrderedCardLoader.getPlayer1Cards();
    public void initalize(){
        handCard = new Card[6];
    }

    public void setHandCard(int x,int id) {
        handCard[x]= player1Cards.get(id);
    }
    public Card getHandCard(int x) {
        return handCard[x];
    }

}


