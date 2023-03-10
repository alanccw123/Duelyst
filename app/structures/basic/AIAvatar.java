package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;

import akka.actor.ActorRef;
import commands.BasicCommands;

public class AIAvatar extends Unit{
    @JsonIgnore
    ActorRef out;
    @JsonIgnore
    Player owner;
   
    @Override
    public void setHealth(int health) {
        super.setHealth(health);
        owner.setHealth(super.getHealth());
        BasicCommands.setPlayer2Health(out, owner);
        if (getHealth() <= 0) {
            BasicCommands.addPlayer1Notification(out, "You win! Enemy avatar 0 health", 10);
        }
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void setActorRef(ActorRef out) {
        this.out = out;
    }
}
