package game;

import java.sql.Date;
import java.util.LinkedList;

public class GameEngine {

	@SuppressWarnings("unused")
	private String[] aiScripts;	// TODO, pull this out into an interface, handle different kinds of scripts for 
	public GameState gameState;
	
	private LinkedList<GameObserver> observers = new LinkedList<>();
	private int winnerPlayerID = -1;
	
	public GameEngine(String[] aiScripts) {
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
				for (Ant ant : colony.ants) {
					AntMove antMove = ant.playTurn();
					notifyObservers(antMove, ant);
				//	display();
				}
			}
			turns--;
		}
		System.out.println("completed");
		notifyObservers(-1);
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
				if (tile.ant != null) {
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
