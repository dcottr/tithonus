package game;

import ai.AntAI;

public class Ant {
	public GameState gState;
	private AntAI ai;
	public final int playerID;
	public final int antID;
	public Tile position;
	public Direction facingDirection;
	public boolean alive = true;
	public float health = 100.0f;

	public Ant(int playerID, GameState gameState, Tile position, Direction facingDirection) {
		this.playerID = playerID;
		this.gState = gameState;
		this.antID = gState.generateUniqueID();
		setPosition(position);
		this.facingDirection = facingDirection;
	}

	public void setPosition(Tile tile) {
		position = tile;
		tile.ant = this;
	}

	public void setAI(AntAI antAI) {
		this.ai = antAI;
	}

	public AntMove playTurn() {
		if (ai == null)
			throw new IllegalStateException("Cannot play turn until ant's AI has been set.");
		return ai.playTurn();
	}

	public void acceptMove(AntMove move) {
		// modify ant's state from move (and notify observers(?))
		if (!move.valid) {
			System.out.println("Invalid move");
			return;
		}
			
		if (move.moveDirection != null) {
			int newX = position.x + move.moveDirection.xDelta;
			int newY = position.y + move.moveDirection.yDelta;
			Tile newPositionTile = gState.getTile(newX, newY);
			position.ant = null;
			newPositionTile = (newPositionTile == null || newPositionTile.obstacle()) ? position : newPositionTile;
			setPosition(newPositionTile);
		}
		if (move.turnDirection != null) {
			facingDirection = move.turnDirection;
		}

	}
	
	public boolean canMoveDirection(Direction d) {
		return canMoveToTile(d.xDelta, d.yDelta);
	}
	
	public boolean canTurnDirection(Direction d) {
		return canMoveToTile(d.xDelta, d.yDelta);
	}
	
	public boolean hasEnemyAntInFront() {
		Ant antInFront = getAntInFront();
		return !(antInFront == null || !antInFront.alive || antInFront.playerID == playerID);
	}
	
	public Ant attackAntInFront() {
		Ant antInFront = getAntInFront();
		antInFront.takeDamage(this);
		return antInFront;
	}
	
	public void takeDamage(Ant attacker) {
		health -= 0.1 * attacker.health;
		if (health <= 0.0f)
			alive = false;
	}
	
	private Ant getAntInFront () {
		Tile frontTile = getTileAtDelta(facingDirection.xDelta, facingDirection.yDelta);
		if (frontTile == null)
			return null;
		return frontTile.ant;
	}
	
	private boolean canMoveToTile(int deltaX, int deltaY) {
		if (Math.abs(deltaX) > 1 || Math.abs(deltaY) > 1) return false;
		Tile dest = getTileAtDelta(deltaX, deltaY);
		if (dest != null) {
			return !dest.obstacle();
		}
		return false;

	}
	
	private Tile getTileAtDelta(int deltaX, int deltaY) {
		return gState.getTile(position.x + deltaX, position.y + deltaY);
	}
}
