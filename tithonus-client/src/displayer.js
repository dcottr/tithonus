var baseUrl = "http://localhost:4000";
var fb = new Firebase("https://tithonus.firebaseio.com");
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
	$("#editor1").hide();
	$("#editor2").hide();
	$('#start').hide();
	gameJSON = data;
	GAME_WIDTH = (gameJSON["boardDimension"]) ["x"] * tileWidth;
	GAME_HEIGHT = (gameJSON["boardDimension"]) ["y"] * tileHeight;
	if (game !== null) {
		game.destroy();
		currentMoveIndex = -1;
	}
	game = new Phaser.Game(GAME_WIDTH, GAME_HEIGHT, Phaser.AUTO, 'match', { preload: preload, create: create, update: update });
}

function runMatch( playerAIs ) {
	var url = baseUrl + "/match/ai";
	var playerID = [1, 2];
	var data = {};
	for (var i = 0; i < playerAIs.length; i++) {
		// data[i] = {"player" : playerAIs[i] };
		data["p" + i] = playerAIs[i];
	}

	var request = $.get(url, data,function(data) {
		startGame(data);
	}, "json");

	request.error(function(jqXHR, textStatus, errorThrown) {
		console.log(errorThrown);
		alert("error: could not connect to server");
	});
}

var editor1 = ace.edit("editor1");
editor1.setTheme("ace/theme/monokai");
editor1.getSession().setMode("ace/mode/lua");
var editor2 = ace.edit("editor2");
editor2.setTheme("ace/theme/monokai");
editor2.getSession().setMode("ace/mode/lua");
$("#start").click(function(event) {
	var ai1Text = editor1.getValue();
	var ai2Text = editor2.getValue();
	saveAI(ai2Text);
	runMatch([ai1Text, ai2Text]);
});

function saveAI(aiText) {
	var aisRef = fb.child("AIs").child(userAuthData.uid);
	aisRef.set({
		ai: aiText
	});
}


// LOGIN/LOGOUT
var userAuthData = null;
var isNewUser = false;
var newUserName = null;
function authDataCallback(authData) {
	console.log("authDataCallback");
	if (authData) {
		console.log("User " + authData.uid + " is logged in with " + authData.provider);
		if (isNewUser) {
			fb.child("users").child(authData.uid).set({
				provider: authData.provider,
				name: newUserName,
			});
			isNewUser = false;
		}
		loggedIn();
		userAuthData = authData;
	} else {
		console.log("User is logged out");
		loggedOut();
	}
}

function getName(authData) {
	switch(authData.provider) {
		case 'password':
		return authData.password.email.replace(/@.*/, '');
		case 'twitter':
		return authData.twitter.displayName;
		case 'facebook':
		return authData.facebook.displayName;
	}
}

loggedOut();

function loggedOut() {
	$("#editor1").hide();
	$("#editor2").hide();
	$("#start").hide();
	$("#btn-register").show();
	$("#btn-login").show();
	$("#btn-logout").hide();
	userAuthData = null;
}

function loggedIn() {
	$("#editor1").show();
	$("#editor2").show();
	$("#start").show();
	$("#btn-register").hide();
	$("#btn-login").hide();
	$("#btn-logout").show();
}
// Register the callback to be fired every time auth state changes
fb.onAuth(authDataCallback);


// Create a callback to handle the result of the authentication
function authHandler(error, authData) {
	if (error) {
		alert(error);
		loggedOut();
	} else {

		console.log("Authenticated successfully with payload:", authData);
		loggedIn();
	}
}

function doLogin(email, password) {
	console.log("logging in, newUser: " + isNewUser);
	fb.authWithPassword({
		email    : email,
		password : password
	}, authHandler);
};

function logout () {
	fb.unauth();
	loggedOut();
}

function registerEmail(name, email, password) {
	isNewUser = true;
	newUserName = name;
	fb.createUser({
		email    : email,
		password : password
	}, function(error, userData) {
		if (error) {
			console.log("Error creating user:", error);
		} else {
			doLogin(email, password);
			console.log("Successfully created user account with uid:", userData.uid);
		}
	});
}

$(function () {
	$( "#dialog" ).dialog();
	$("#dialog-register").dialog(
	{
		autoOpen: false,
		buttons: {
			"ok": function () {
				var name = $("#register-name").val();
				var email = $("#register-email").val();
				var password = $("#register-password").val();
				registerEmail(name, email, password);
				$(this).dialog("close");
			},
			Cancel: function () {
				$(this).dialog("close");
			}
		}
	});

	$("#dialog-login").dialog({
		autoOpen: false,
		buttons: {
			"ok": function () {
				var email = $("#login-email").val();
				var password = $("#login-password").val();
				doLogin(email, password);
				$(this).dialog("close");
			},
			Cancel: function () {
				$(this).dialog("close");
			}
		}
	});

	$("#btn-register").click(function () {
		$("#dialog-register").dialog("open");
	});

	$("#btn-login").click(function () {
		$("#dialog-login").dialog("open");
	});

	$("#btn-logout").click(function () {
		logout();
	});
});


