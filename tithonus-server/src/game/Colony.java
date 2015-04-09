package game;

import java.util.Random;

import ai.LuaJManager;

public class Colony {
	
	private GameState gState;
	public int playerID;
	public Ant[] ants;
	public static final int PLAYER_ANTS_COUNT = 15;
	

	public Colony(String aiScript, int playerID, GameState gameState) {
		this.playerID = playerID;
		ants = new Ant[PLAYER_ANTS_COUNT];
		this.gState = gameState;
		int antID = 0;
		// populate ants with playerIDs
		for (int i = 0; i < PLAYER_ANTS_COUNT; i++, antID++) {
			// TODO: have proper initial ant positions
			Tile tile;
			do {
				tile = gState.getTile(gState.random.nextInt(gState.WORLD_X_LENGTH), gState.random.nextInt(gState.WORLD_Y_LENGTH));
			} while (tile.obstacle());
			Direction direction =  Direction.values()[gState.random.nextInt(Direction.values().length)];
			ants[i] = new Ant(playerID, gameState, tile, direction);
			LuaJManager.setupScripts(aiScript, ants[i]);
		}
	}
	
	
}
