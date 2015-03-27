package ai;
import game.Ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.*;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;


/*
 * Receive AI Scripts
 * Setup game (map, placement etc)
 * 	Each Colony has a LuaJManager
 * 	luaj required obj calls singleton to set playerID, set itself as game observer, get stuff for sensor info.
 * 
 * 
 * 
 * */



public class LuaJManager {
	
	private LinkedList<LuaAnt> luaAnts;
	
	public LuaJManager() {
		luaAnts = new LinkedList<>();
	}

	/*public static void main(String[] args) throws FileNotFoundException {
		String[] filePaths = new String[]{"AIScripts/ai1", "AIScripts/ai2"};
		String[] aiScripts = new String[filePaths.length];
		Ant[] ants = new Ant[filePaths.length];
		for (int i = 0; i < filePaths.length; i++) {
			//read the script from path
			aiScripts[i] = "";
			Scanner scanner = new Scanner(new File(filePaths[i]));
			while (scanner.hasNextLine()) {
				aiScripts[i] += scanner.nextLine() + '\n';
			}
			scanner.close();
			
			ants[i] = new Ant(i);
		}
		
		LuaJManager manager = new LuaJManager();
		manager.setupScripts(aiScripts, ants);
		for (LuaAnt luaAnt : manager.luaAnts) {
			luaAnt.playTurn();
		}
	}*/
	
	public static void setupScripts(String luaScript, Ant ant) {
		Globals globals = JsePlatform.standardGlobals();
		LuaValue script = globals.load(luaScript);
		LuaAnt luaAnt = new LuaAnt(globals, script, ant);
		ant.setAI(luaAnt);
	}
	

}



/* Example code
 * 
 * 		// create an environment to run in
		Globals globals = JsePlatform.standardGlobals();
		
		Globals globalstmp = JsePlatform.standardGlobals();
		
		// Use the convenience function on Globals to load a chunk.
		LuaValue chunk = globals.loadfile(filePath);
		LuaValue chunktmp = globalstmp.loadfile(filePath);
		
		globals.set("global", new GameObject());
		LuaFunction k = new GameObject();
		// Use any of the "call()" or "invoke()" functions directly on the chunk.
				
		globals.set("player", 1);
		globalstmp.set("player", 0);
		chunk.call();
		chunktmp.call();
 * 
 * */
