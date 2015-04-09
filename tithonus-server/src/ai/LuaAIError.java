package ai;

import org.luaj.vm2.LuaError;

public class LuaAIError extends Throwable {
	private int playerID;
	private LuaError luaError;
    public LuaAIError( LuaError luaError, int playerID) {
        super(luaError.getMessage());
        this.playerID = playerID;
        this.luaError = luaError;
     }
    
    public String getMessage() {
    	return luaError.getMessage();
    }
    
    public int getPlayerID() {
    	return playerID;
    }
}
