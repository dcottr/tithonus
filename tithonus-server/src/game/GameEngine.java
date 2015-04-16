package game;

import java.util.LinkedList;

import ai.LuaAIError;

public class GameEngine {

	@SuppressWarnings("unused")
	private String[] aiScripts;	// TODO, pull this out into an interface, handle different kinds of scripts for 
	public GameState gameState;
	
	private LinkedList<GameObserver> observers = new LinkedList<>();
	private int winnerPlayerID = -1;
	
	public GameEngine(String[] aiScripts) throws LuaAIError {
		this.aiScripts = aiScripts;
		gameState = new GameState(aiScripts);
	}
		
	public void start() {
		int turns = 100;
		System.out.println("go");
		while (winnerPlayerID < 0 && turns > 0) {
			// play a game's turn
			// foreach colony, play turn -> check victory
			for (Colony colony : gameState.colonies) {
				boolean colonyAlive = false;
				for (Ant ant : colony.ants) {
					if (ant.alive) {
						AntMove antMove = ant.playTurn();
						notifyObservers(antMove, ant);
						colonyAlive = true;
					}
				}
				if (!colonyAlive) {
					winnerPlayerID = getWinnerID();
					break;
				}
			}
			turns--;
		}
		System.out.println("completed");
		
		notifyObservers(getWinnerID());
	}
	
	private int getWinnerID() {
		int[] livingAnts = new int[gameState.colonies.length];
		for (int i = 0; i < gameState.colonies.length; i++) {
			Colony colony = gameState.colonies[i];
			for (Ant ant : colony.ants) {
				if (ant.alive) {
					livingAnts[i]++;
				}
			}
		}
		// To be modified for matches with more than two players.
		int winner = 0;
		boolean tie = false;
		for (int i = 1; i < livingAnts.length; i++){
			int newnumber = livingAnts[i];
			if (newnumber > livingAnts[winner]){
				winner = i;
				tie = false;
			} else if (newnumber == livingAnts[winner]) {
				tie = true;
			}
		}
		if (tie) {
			return -1;
		} else
			return winner;
	}
	
	public void addObserver(GameObserver observer) {
		observers.add(observer);
	}
	
	public void removeObserver(GameObserver observer) {
		observers.remove(observer);
	}
	
	private void notifyObservers(AntMove antMove, Ant ant) {
		if (observers == null) return;
		for (GameObserver observer : observers) {
			observer.notifyMove(antMove, ant);
		}
	}
	
	private void notifyObservers(int winningPlayerID) {
		if (observers == null) return;
		for (GameObserver observer : observers) {
			observer.notifyOutcome(winningPlayerID);
		}
	}

	
		
	private void display() {
		Tile tile;
		for (int y = 0; y < gameState.WORLD_Y_LENGTH; y++) {
			for (int x = 0; x < gameState.WORLD_X_LENGTH; x++) {
				tile = gameState.getTile(x, y);
				if (tile.ant != null && tile.ant.alive) {
					char c = '?';
					switch (tile.ant.facingDirection) {
					case N:
						c = '^';
						break;
					case E:
						c = '>';
						break;
					case S:
						c = 'v';
						break;
					case W:
						c = '<';
						break;
					}
					
					System.out.print(c);
				} else {
					System.out.print('O');
				}
				if (x != gameState.WORLD_X_LENGTH - 1) {
					System.out.print(' ');
				}
			}
			System.out.println();
		}
	}
}
