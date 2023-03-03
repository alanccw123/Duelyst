package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import commands.BasicCommands;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	int id;
	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;
	
	

	@JsonIgnore
	private int player;
	@JsonIgnore
	private int health;
	@JsonIgnore
	private int attack;
	@JsonIgnore
	private Tile current;


	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public Tile getTile() {
		return current;
	}

	@JsonIgnore
	private int maxAttackAction = 1;
	@JsonIgnore
	private int availableAttackAction = 0;
	@JsonIgnore
	private int maxMoveAction = 1;
	@JsonIgnore
	private int availableMoveAction = 0;

	public void setMaxAttackAction(int i) {
		maxAttackAction = i;
	}

	public boolean canMove() {
		return availableMoveAction > 0;
	}
	
	public boolean canAttack() {
		return availableAttackAction > 0;
	}

	public void resetAction() {
		availableAttackAction = maxAttackAction;
		availableMoveAction = maxMoveAction;
	}

	public void spendAttackAction() {
		availableAttackAction--;
		availableMoveAction--;
	}

	public void spendMoveAction() {
		availableMoveAction--;
	}

	@JsonIgnore
	private boolean isProvoke;
	@JsonIgnore
	private boolean isRanged;
	@JsonIgnore
	private boolean isflying;

	public boolean isProvoke() {
        return isProvoke;
    }

	public void setprovoke(boolean provoke) {
		isProvoke = provoke;
	}

    public boolean isRanged() {
        return isRanged;
    }

	public void setRanged(boolean ranged) {
		isRanged = ranged;
	}
    public boolean isIsflying() {
        return isflying;
    }

	public void setFlying(boolean flying) {
		isflying = flying;
	}

    public Unit() {}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
	}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
	}
	
	
	
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}
	
	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
		this.current = tile;
		tile.setUnit(this);
	}
	
	
}
