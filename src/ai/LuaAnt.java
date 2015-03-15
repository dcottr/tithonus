package ai;
import game.Ant;
import game.AntMove;
import game.GameState;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;


// LuaJManager --> LuaAnt --> ant
// 
// AntAction class (to evaluate turn actions)
// 

public class LuaAnt extends AntAI {

	public int playerID;
	private Globals env;
	private LuaValue script;
	private AntMove move;
	
	public LuaAnt(Globals globals, LuaValue script, Ant ant) {
		this.env = globals;
		this.script = script;
		this.ant = ant;
		this.playerID = ant.playerID;
		setup();
	}
	
	private void setup() {
		setEnv("player", playerID);
		setEnv("yodle", new Yodle());
		setEnv("moveForward", new MoveForward());
		setEnv("turnLeft", new TurnLeft());
		setEnv("turnRight", new TurnRight());
		updateLuaEnv();
	}
	
	// return an AntMove object
	public AntMove playTurn() {
		move = new AntMove(ant);
		script.call();
		ant.acceptMove(move);
		updateLuaEnv();
		return move;
	}
	
	private class Yodle extends OneArgFunction {
		public LuaValue call(LuaValue x) {
			return LuaValue.valueOf("yodlee" + x);
		}
	}
	 
	 private class MoveForward extends ZeroArgFunction {
		 public LuaValue call() {
			 if (!ant.canMoveDirection(ant.facingDirection))
				 return LuaValue.FALSE;
			 move.setMoveDirection(ant.facingDirection);
			 return LuaValue.TRUE;
		 }
	 }
	 
	 private class TurnLeft extends ZeroArgFunction {
		 public LuaValue call() {
			 move.setTurnDirection(ant.facingDirection.left());
			 return LuaValue.NIL;
		 }
	 }

	 private class TurnRight extends ZeroArgFunction {
		 public LuaValue call() {
			 move.setMoveDirection(ant.facingDirection.right());
			 return LuaValue.NIL;
		 }
	 }

	 
	private void updateLuaEnv() {
		setEnv("xPosition", ant.position.x);
		setEnv("yPosition", ant.position.y);
		setEnv("facingDirection", LuaValue.valueOf(ant.facingDirection.name()));
		
	}
	
	private void setEnv(String varNameString, LuaValue variable) {
		env.set(varNameString, variable);
	}
	
	private void setEnv(String varNameString, int variable) {
		env.set(varNameString, variable);
	}
}
