package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Scanner;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import org.omg.CORBA.PUBLIC_MEMBER;

import ai.LuaJManager;

public class GameEngine {

	private String[] aiScripts;	// TODO, pull this out into an interface, handle different kinds of scripts for 
	private GameState gameState;
	
	private Collection<MoveObserver> observers;
	private int winnerPlayerID = -1;
	
	public GameEngine(String[] aiScripts, Collection<MoveObserver> observers) {
		this.aiScripts = aiScripts;
		this.observers = observers;
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
					display();
				}
			}
			turns--;
		}
	}
	
	private void notifyObservers(AntMove antMove, Ant ant) {
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
	
	public static void main(String[] args) throws FileNotFoundException {
		String[] filePaths = new String[]{"AIScripts/ai1", "AIScripts/ai2"};
		String[] aiScripts = new String[filePaths.length];
		for (int i = 0; i < filePaths.length; i++) {
			//read the script from path
			aiScripts[i] = "";
			Scanner scanner = new Scanner(new File(filePaths[i]));
			while (scanner.hasNextLine()) {
				aiScripts[i] += scanner.nextLine() + '\n';
			}
			scanner.close();
		}
		
		GameEngine engine = new GameEngine(aiScripts);
		engine.start();
	}
}
