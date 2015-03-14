package game;

public abstract class MoveObserver {
	
	public abstract void notifyMove(AntMove move, Ant ant);
}
