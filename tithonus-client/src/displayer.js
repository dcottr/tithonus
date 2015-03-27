var baseUrl = "http://localhost:4000";

var ants = [];
var players = []; // defines initial state
var playerIDs = [];
var moves = [];

var tileWidth = 32;
var tileHeight = 32;
var timeSpeed = 0.01;//1.0; // seconds per turn
var timeElapsed = 0;
var GAME_WIDTH; //= tileWidth * 10;
var GAME_HEIGHT; //= tileHeight * 10;
var game = null;
var gameJSON;

//var game = new Phaser.Game(GAME_WIDTH, GAME_HEIGHT, Phaser.AUTO, 'match', { preload: preload, create: create, update: update });

function preload() {
	game.load.image('tile', 'assets/boundTile.png');

	var antImages = ["ant-red.png", "ant-orange.png"];
	for (var i = 0; i < antImages.length; i++) {
		game.load.image('ant' + i, 'assets/' + antImages[i]);
	}
}

function create() {
	lastGameUpdate = game.time.now;
	for (var i = 0; i < 10; i++) {
		for (var j = 0; j < 10; j++) {
			var tile = game.add.sprite(i * tileWidth, j * tileHeight, 'tile');
		//	var tile = game.add.sprite((i * tileWidth) + 10, (j * tileHeight), 'ant');
		}
	}

	players = gameJSON["players"];
	moves = gameJSON["moves"];
	console.log("Moves count: " + moves.length);
	playerCount = players.length;
	for (var i = 0; i < players.length; i++) {
		var player = players[i];
		playerIDs.push(player["playerID"]);
		var playerAnts = player["ants"];
		for (var j = 0; j < playerAnts.length; j++) {
			var ant = {};
			ant.data = playerAnts[j];
			ant.sprite = game.add.sprite(-1, -1, 'ant' + player["playerID"]);
			ant.sprite.anchor.setTo(0.5, 0.5);
			ants.push(ant);
		}
	}
	displayCurState();

}

var lastGameUpdate;
var currentMoveIndex = -1;
function update() {
	if (game.time.elapsedSince(lastGameUpdate) > timeSpeed * 1000) {
		currentMoveIndex++;
		console.log("index: " + currentMoveIndex);
		if (currentMoveIndex < moves.length) {
			applyMoveToState(moves[currentMoveIndex]);
			displayCurState();
		} else {
			// TODO: display winner is playerID: gameJSON["winnerPlayerID"]
		}
//		game.debug.text('Here: ' + (k++), 32, 32);
		lastGameUpdate = game.time.now;
	}

}

// change ants[i]
function applyMoveToState(move) {
	var movedAnts = move["modifiedAnts"];
	console.log("modifiedAnts: " + movedAnts);
	if (movedAnts !== null) {
		for (var i = 0; i < movedAnts.length; i++) {
			var movedAnt = movedAnts[i];
			var antID = movedAnt["antID"];
			var antIndex = -1;
			for (var j = 0; j < ants.length; j++) {
				if (ants[j].data["antID"] === antID) {
					antIndex = j;
				}
			}
			if (antIndex > -1) {
				ants[antIndex].data = movedAnt;
			}
		}
	}
}

function displayCurState() {
	// TODO: display ants.
	for (var i = 0; i < ants.length; i++) {
		// display ant with colour corresponding to ant["playerID"]
		var data = ants[i].data;
		//console.log(data);
		var x = (data["position"])["x"];
		var y = (data["position"])["y"];
		var dir = data["facingDirection"];
		var antSprite = ants[i].sprite;
		antSprite.x = x * tileWidth + tileWidth/2.0;
		antSprite.y = y * tileHeight + tileHeight/2.0;
		setSpriteDir(ants[i].sprite, data["facingDirection"]);
		if (!data["alive"]) {
			antSprite.visible = false;
		}
	}
}

// Defaults to facing north
function setSpriteDir(sprite, facingDirection) {
	var angle = 0; // N
	if (facingDirection === "E") {
		angle = 90;
	} else if (facingDirection == "S") {
		angle = 180;
	} else if (facingDirection === "W") {
		angle = -90;
	}
	sprite.angle = angle;
}

function startGame(data) {

	gameJSON = data;
	GAME_WIDTH = (gameJSON["boardDimension"]) ["x"] * tileWidth;
	GAME_HEIGHT = (gameJSON["boardDimension"]) ["y"] * tileHeight;
	if (game !== null) {
		game.destroy();
		currentMoveIndex = -1;
	}
	game = new Phaser.Game(GAME_WIDTH, GAME_HEIGHT, Phaser.AUTO, 'match', { preload: preload, create: create, update: update });
}

function runMatch( playerID ) {
	var url = baseUrl + "/match/ai?";
	for (var i = 0; i < playerID.length; i++) {
		url += "id=" + playerID[i];
		if (i !== playerID.length - 1)
			url += "&";
	}
	var request = $.get(url, function(data) {
		for(var k in data)
			console.log(k)
		console.log((data["boardDimension"]) ["x"])
		startGame(data);

	}, "json");

	request.error(function(jqXHR, textStatus, errorThrown) {
		console.log(errorThrown);
		alert("error: could not connect to server");
	});
};

document.getElementById("fight").addEventListener("click", function(event) {
	var playerID = [];
	playerID.push(document.getElementById("p1").value);
	playerID.push(document.getElementById("p2").value);
	runMatch(playerID);
});