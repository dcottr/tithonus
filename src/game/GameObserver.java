package game;

public abstract class GameObserver {
	
	public GameState gameState;
	
	public GameObserver(GameState gameState) {
		this.gameState = gameState;
	}
	
	// This must be called after the move has been fully applied to the GameState
	public abstract void notifyMove(AntMove move, Ant ant);
	
	// winningPlayerID is -1 if tied
	public abstract void notifyOutcome(int winningPlayerID);
	
}
