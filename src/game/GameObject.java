package game;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.*;

/*print('xPosition: ', sensor.xPos);
action.moveForward();
print('xPosition: ', sensor.xPos);
action.moveForward();
print('xPosition: ', sensor.xPos);
print('yodle: ', action.yodle("ehoo"));
print('player:', sensor.getPlayerID());*/

public class GameObject extends TwoArgFunction{

	public int playerID;
	private int xPos = 34;
	private int yPos = 21;
	private LuaValue luaEnv;
	
	public GameObject() {
		// Set PlayerID from singleton
		playerID = 1;
	}

	// First call
	public LuaValue call(LuaValue modname, LuaValue env) {
		luaEnv = env;
		LuaValue sensor = tableOf();
		LuaValue action = tableOf();
		sensor.set("xPos", xPos);
		sensor.set("yPos", yPos);
		sensor.set("getPlayerID", new PlayerID());
		action.set("yodle", new yodle());
		action.set("moveForward", new MoveForward());
		env.set("sensor", sensor);
		env.set("action", action);
		return LuaValue.NIL;
	}
	
	public void updateLuaEnv() {
		luaEnv.get("sensor").set("xPos", xPos);
		luaEnv.get("sensor").set("yPos", yPos);
	}
	
	 private class yodle extends OneArgFunction {
		public LuaValue call(LuaValue x) {
			return LuaValue.valueOf("yodlee" + x);
		}
	}
	 
	 private class MoveForward extends ZeroArgFunction {
		 public LuaValue call() {
			 xPos += 1;
			 updateLuaEnv();
			 return LuaValue.NIL;
		 }
	 }
	
	 private class PlayerID extends ZeroArgFunction {
		 public LuaValue call() {
			 return LuaValue.valueOf(playerID);
		 }
	 }

}
