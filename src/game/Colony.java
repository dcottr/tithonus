package game;

import ai.LuaJManager;

public class Colony {
	
	private GameState gState;
	public int playerID;
	public Ant[] ants;
	public static final int PLAYER_ANTS_COUNT = 5;
	

	public Colony(String aiScript, int playerID, GameState gameState) {
		this.playerID = playerID;
		ants = new Ant[PLAYER_ANTS_COUNT];
		this.gState = gameState;
		int antID = 0;
		// populate ants with playerIDs
		for (int i = 0; i < PLAYER_ANTS_COUNT; i++, antID++) {
			// TODO: have proper initial ant positions
			ants[i] = new Ant(playerID, gameState, gState.getTile(playerID * 7 + 1, i + 2), Direction.E);
			LuaJManager.setupScripts(aiScript, ants[i]);
		}
	}
	
	
}
