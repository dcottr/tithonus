package server;

import game.Ant;
import game.AntMove;
import game.Colony;
import game.GameState;
import game.MoveObserver;

import javax.json.*;

public class GameEncoder extends MoveObserver {

	private JsonObjectBuilder game = Json.createObjectBuilder();
	private JsonArrayBuilder moves = Json.createArrayBuilder();

	public GameEncoder(GameState gameState) {
		super(gameState);
		game.add("boardDimension", Json.createObjectBuilder()
				.add("x", gameState.WORLD_X_LENGTH)
				.add("y", gameState.WORLD_X_LENGTH));
		game.add("players", encodePlayers());
	}
	
	@Override
	public void notifyMove(AntMove move, Ant ant) {
		JsonObjectBuilder moveBuilder = encodeMove(move, ant);
		moves.add(moveBuilder);
	}
	
	public JsonObject getGameJson() {
		game.add("moves", moves);
		return game.build();
	}
	
	private JsonArrayBuilder encodePlayers() {
		JsonArrayBuilder players = Json.createArrayBuilder();
		
		for (Colony colony : gameState.colonies) {
			JsonObjectBuilder player = Json.createObjectBuilder();
			player.add("playerId", colony.playerID);
			JsonArrayBuilder ants = Json.createArrayBuilder();
			for (Ant ant : colony.ants) {
				ants.add(encodeAnt(ant));
			}
			player.add("ants", ants);
			players.add(player);
		}
		
		return players;
	}
	
	private JsonObjectBuilder encodeAnt(Ant ant) {
		JsonObjectBuilder antBuilder = Json.createObjectBuilder();
		antBuilder.add("playerID", ant.playerID);
		antBuilder.add("antID", ant.antID);
		antBuilder.add("alive", ant.alive);
		antBuilder.add("facingDirection", ant.facingDirection.toString());
		antBuilder.add("position", Json.createObjectBuilder()
				.add("x", ant.position.x)
				.add("y", ant.position.y));
		return antBuilder;
	}
	
	@SuppressWarnings("unused")
	private JsonObjectBuilder encodeMove(AntMove move, Ant ant) {
		JsonObjectBuilder moveBuilder = Json.createObjectBuilder();
		JsonArrayBuilder changesBuilder = Json.createArrayBuilder();
		changesBuilder.add(encodeAnt(ant));
		moveBuilder.add("movedAnts", changesBuilder);
		return moveBuilder;
	}	
}
