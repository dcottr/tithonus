package game;

import java.util.LinkedList;

public class GameEngine {

	@SuppressWarnings("unused")
	private String[] aiScripts;	// TODO, pull this out into an interface, handle different kinds of scripts for 
	public GameState gameState;
	
	private LinkedList<MoveObserver> observers = new LinkedList<>();
	private int winnerPlayerID = -1;
	
	public GameEngine(String[] aiScripts) {
		this.aiScripts = aiScripts;
		gameState = new GameState(aiScripts);
	}
		
	public void start() {
		int turns = 15;
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
	}
	
	public void addObserver(MoveObserver observer) {
		observers.add(observer);
	}
	
	public void removeObserver(MoveObserver observer) {
		observers.remove(observer);
	}
	
	private void notifyObservers(AntMove antMove, Ant ant) {
		if (observers == null) return;
		for (MoveObserver observer : observers) {
			observer.notifyMove(antMove, ant);
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
