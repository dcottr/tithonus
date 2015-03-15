package game;

public class AntMove {
	
	// TODO: only allow one operation at a time.
	public Direction moveDirection = null;
	public Direction turnDirection = null;
	
	public boolean valid = true;
	private boolean moveAssigned = false;
	
	public Ant ant;
	
	public AntMove(Ant ant) {
		this.ant = ant;
	}

	public void setMoveDirection(Direction dir) {
		certifyMove();
		moveDirection = dir;
	}
	
	public void setTurnDirection(Direction dir) {
		certifyMove();
		turnDirection = dir;
	}
	
	// Call once for each move received.
	private void certifyMove() {
		// Multiple moves in one turn are not allowed
		valid = !moveAssigned;
		moveAssigned = true;
	}
}
