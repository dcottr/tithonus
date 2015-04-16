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

	private final int OBSTACLE_COUNT = 10;
	
	
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
		
		for (int i = 0; i < OBSTACLE_COUNT; i++) {
			Tile tile;
			do {
				tile = getRandomTile();
			} while (tile.filled());
			tile.setObstacle(true);
		}
		
		// Initialize player colonies
		colonies = new Colony[playerCount];
		for (int i = 0; i < colonies.length; i++) {
			colonies[i] = new Colony(aiScripts[i], i, this);
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
	
	public Tile getRandomTile() {
		return getTile(random.nextInt(WORLD_X_LENGTH), random.nextInt(WORLD_Y_LENGTH));
	}
	
	// Manhattan distance
	public static int dist(Tile a, Tile b) {
		return (int)Math.abs(a.x - b.x + a.y - b.y);
	}
}
