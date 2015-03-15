package server;

import game.GameEngine;
import game.GameState;
import game.MoveObserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonWriter;

// TODO: this is a biggie.... make this into an actual server

public class Server {

	
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
		GameEncoder encoder = new GameEncoder(engine.gameState);
		engine.addObserver(encoder);
		engine.start();
		
		//write to file
        OutputStream os = new FileOutputStream("game.json");
        JsonWriter jsonWriter = Json.createWriter(os);
        jsonWriter.writeObject(encoder.getGameJson());
        jsonWriter.close();
	}
}
