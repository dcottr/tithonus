package game;

import java.util.ArrayList;

public class AntMove {
	
	// TODO: only allow one operation at a time.
	public Direction moveDirection = null;
	public Direction turnDirection = null;
	
	public boolean valid = true;
	private boolean moveAssigned = false;
	
	public Ant ant;
	public ArrayList<Ant> modifiedAnts = new ArrayList<>();
	
	public AntMove(Ant ant) {
		this.ant = ant;
		modifiedAnts.add(ant);
	}

	public void setMoveDirection(Direction dir) {
		certifyMove();
		moveDirection = dir;
	}
	
	public void setTurnDirection(Direction dir) {
		certifyMove();
		turnDirection = dir;
	}
	
	public float attackAntInFront() {
		certifyMove();
		Ant attackedAnt = ant.attackAntInFront();
		if (attackedAnt == null) return 0.0f;
		modifiedAnts.add(attackedAnt);
		return Math.min(0.0f, attackedAnt.health);
	}
	
	// Call once for each move received.
	private void certifyMove() {
		// Multiple moves in one turn are not allowed
		valid = !moveAssigned;
		moveAssigned = true;
	}
}
