package ai;
import game.Ant;
import game.AntMove;


public abstract class AntAI {
	protected Ant ant;
	public abstract AntMove playTurn();
}
