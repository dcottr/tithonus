package game;

public enum Direction {
	N	(0, -1),
	E	(1, 0),
	S	(0, 1),
	W	(-1, 0);
	
	public final int xDelta;
	public final int yDelta;
	Direction(int xDelta, int yDelta) {
		this.xDelta = xDelta;
		this.yDelta = yDelta;
	}
	
	public Direction left() {
		switch (this) {
			case N: return W;
			case E: return N;
			case S: return E;
			case W: return S;
		}
        throw new AssertionError("Unknown dir: " + this);
	}
	
	public Direction right() {
		switch (this) {
			case N: return E;
			case E: return S;
			case S: return W;
			case W: return N;
		}
        throw new AssertionError("Unknown dir: " + this);
	}
}

