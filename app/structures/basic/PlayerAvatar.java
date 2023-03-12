package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;

import akka.actor.ActorRef;
import commands.BasicCommands;

/*
 * This class extends the base Unit class and represents the player avatar
 */
public class PlayerAvatar extends Unit{
    @JsonIgnore
    ActorRef out;
    @JsonIgnore
    Player owner;
   
    // overides the default setHealth method to propagate any health change to the human player
    @Override
    public void setHealth(int health) {
        super.setHealth(health);
        owner.setHealth(getHealth());
        BasicCommands.setPlayer1Health(out, owner);

         // if health equals to zero, Player loses the game
        if (getHealth() <= 0) {
            BasicCommands.addPlayer1Notification(out, "You lose! Your avatar 0 health", 10);
        }
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void setActorRef(ActorRef out) {
        this.out = out;
    }
    
}
