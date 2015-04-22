package ai;
import game.Ant;
import game.AntMove;
import game.Colony;
import game.Tile;
import game.Direction;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
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
		setEnv("moveForward", new MoveForward());
		setEnv("turnLeft", new TurnLeft());
		setEnv("turnRight", new TurnRight());
		setEnv("turn", new Turn());
		setEnv("enemyAntAhead", new EnemyAntInFront());
		setEnv("attackAntAhead", new AttackAntInFront());
		setEnv("callForHelp", new CallForHelp());
		setEnv("hearCallForHelp", new HearCallForHelp());
		updateLuaEnv();
	}
	
	// return an AntMove object
	public AntMove playTurn() {
		move = new AntMove(ant);
		try {
			env.get("playTurn").call();
		} catch (LuaError e) {
			throw new LuaAIError(e, ant.playerID);
		}
		ant.acceptMove(move);
		updateLuaEnv();
		return move;
	}
	
	 private class Turn extends OneArgFunction {
		 public LuaValue call(LuaValue arg) {
			 String shortDir = arg.strvalue().tojstring();
			 Direction dir = Direction.valueOf(shortDir);
			 move.setTurnDirection(dir);
			 return LuaValue.TRUE;
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
			 move.setTurnDirection(ant.facingDirection.right());
			 return LuaValue.NIL;
		 }
	 }
	 
	 private class EnemyAntInFront extends ZeroArgFunction {
		 public LuaValue call() {
			 return LuaValue.valueOf(ant.hasEnemyAntInFront());
		 }
	 }
	 
	 // Returns new health of enemy (zero if dead)
	 private class AttackAntInFront extends ZeroArgFunction {
		 public LuaValue call() {
			 return LuaValue.valueOf(move.attackAntInFront());
		 }
	 }
	 
	 private class CallForHelp extends ZeroArgFunction {
		 public LuaValue call() {
			 move.setCallForHelp();
			 return LuaValue.valueOf(true);
		 }
	 }

	 private class HearCallForHelp extends ZeroArgFunction {
		 public LuaValue call() {
			 return LuaValue.valueOf(ant.hearCallForHelp());
		 }
	 }
	 
	private void updateLuaEnv() {
		setEnv("xPosition", ant.position.x);
		setEnv("yPosition", ant.position.y);
		
		Ant nearestCall = ant.nearestAllyCallForHelp();
		if (nearestCall == null || nearestCall.getTileInFront() == null) {
			setEnv("nearestCall_xPosition", -1);
			setEnv("nearestCall_yPosition", -1);
		} else {
			Tile frontTile = nearestCall.getTileInFront();
			setEnv("nearestCall_xPosition", frontTile.x);
			setEnv("nearestCall_yPosition", frontTile.y);
		}

		setEnv("myHitpoints", LuaValue.valueOf(ant.health));
		setEnv("facingDirection", LuaValue.valueOf("" + ant.facingDirection.name().charAt(0)));
	}
	
	private void setEnv(String varNameString, LuaValue variable) {
		env.set(varNameString, variable);
	}
	
	private void setEnv(String varNameString, int variable) {
		env.set(varNameString, variable);
	}
}
