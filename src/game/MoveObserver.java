package game;

public abstract class MoveObserver {
	
	public GameState gameState;
	
	public MoveObserver(GameState gameState) {
		this.gameState = gameState;
	}
	
	// This must be called after the move has been fully applied to the GameState
	public abstract void notifyMove(AntMove move, Ant ant);
	
}
