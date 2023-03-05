package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;

import akka.actor.ActorRef;
import commands.BasicCommands;

public class PlayerAvatar extends Unit{
    @JsonIgnore
    ActorRef out;
    @JsonIgnore
    Player owner;
   
    @Override
    public void setHealth(int health) {
        super.setHealth(health);
        owner.setHealth(getHealth());
        BasicCommands.setPlayer1Health(out, owner);
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void setActorRef(ActorRef out) {
        this.out = out;
    }
    
}
