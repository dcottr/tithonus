package game;

import java.util.Random;

import org.luaj.vm2.LuaError;

import ai.LuaAIError;


public class GameState {
	
	public Colony[] colonies; // indexed by playerID
	private Tile[][] world; // world[x][y]
	public Random random;
	
	public final int WORLD_X_LENGTH = 20; // x
	public final int WORLD_Y_LENGTH = 20; // y

	
	
	public GameState(String[] aiScripts) throws LuaAIError {
		random = new Random();
		int playerCount = aiScripts.length;
		
		// initialize world tiles
		world = new Tile[WORLD_X_LENGTH][WORLD_Y_LENGTH];
		for (int x = 0; x < WORLD_X_LENGTH; x++) {
			for (int y = 0; y < WORLD_Y_LENGTH; y++) {
				world[x][y] = new Tile(x, y);
			}
		}
		// Initialize player colonies
		colonies = new Colony[playerCount];
		for (int i = 0; i < colonies.length; i++) {
			try {
				colonies[i] = new Colony(aiScripts[i], i, this);
			} catch (LuaError e) {
				throw new LuaAIError(e, i);
			}
		}
	}
	
	private int uniqueID = 0;
	// generates ID unique to this instance of GameState.
	public int generateUniqueID() {
		return uniqueID++;
	}

	
	
	/**
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @return World tile, null if out of bounds
	 */
	public Tile getTile(int x, int y) {
		return (x >= 0 && x < WORLD_X_LENGTH && y >=0 && y < WORLD_Y_LENGTH) ? world[x][y] : null;
	}
}
