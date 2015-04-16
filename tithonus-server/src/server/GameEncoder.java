package server;

import game.Ant;
import game.AntMove;
import game.Colony;
import game.GameState;
import game.GameObserver;

import javax.json.*;

public class GameEncoder extends GameObserver {

	private JsonObjectBuilder game = Json.createObjectBuilder();
	private JsonArrayBuilder moves = Json.createArrayBuilder();
	
	public GameEncoder(GameState gameState) {
		super(gameState);
		game.add("boardDimension", Json.createObjectBuilder()
				.add("x", gameState.WORLD_X_LENGTH)
				.add("y", gameState.WORLD_X_LENGTH));
		game.add("players", encodePlayers());
		game.add("obstacles", encodeObstacles());
	}
	
	@Override
	public void notifyMove(AntMove move, Ant ant) {
		JsonObjectBuilder moveBuilder = encodeMove(move, ant);
		moves.add(moveBuilder);
	}
	
	@Override
	public void notifyOutcome(int winningPlayerID) {
		game.add("winnerPlayerID", winningPlayerID);
	}
	
	public JsonObject getGameJson() {
		game.add("moves", moves);
		return game.build();
	}
	
	private JsonArrayBuilder encodeObstacles() {
		JsonArrayBuilder obstacles = Json.createArrayBuilder();
		for (int x = 0; x < gameState.WORLD_X_LENGTH; x++) {
			for (int y = 0; y < gameState.WORLD_Y_LENGTH; y++) {
				if (gameState.getTile(x, y).obstacle()) {
					JsonObjectBuilder obstacleBuilder = Json.createObjectBuilder();
					obstacleBuilder.add("position", Json.createObjectBuilder()
							.add("x", x)
							.add("y", y));
					obstacles.add(obstacleBuilder);
				}
			}
		}
		return obstacles;
	}
	
	private JsonArrayBuilder encodePlayers() {
		JsonArrayBuilder players = Json.createArrayBuilder();
		
		for (Colony colony : gameState.colonies) {
			JsonObjectBuilder player = Json.createObjectBuilder();
			player.add("playerID", colony.playerID);
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
		for (Ant changedAnt : move.modifiedAnts) {
			changesBuilder.add(encodeAnt(changedAnt));
		}
		moveBuilder.add("modifiedAnts", changesBuilder);
		moveBuilder.add("antID", ant.antID);
		moveBuilder.add("callForHelp", move.callForHelp);
		return moveBuilder;
	}

	
}
