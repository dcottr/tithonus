var baseUrl = "http://localhost:4000";
var fb = new Firebase("https://tithonus.firebaseio.com");
var ants = [];
var players = []; // defines initial state
var obstacles = [];
var playerIDs = [];
var moves = [];

var tileWidth = 32;
var tileHeight = 32;
var timeSpeed = 0.001;//1.0; // seconds per turn
var timeElapsed = 0;
var GAME_WIDTH; //= tileWidth * 10;
var GAME_HEIGHT; //= tileHeight * 10;
var game = null;
var gameJSON;


function preload() {
	game.load.image('tile', 'assets/plainTile.png');

	var antImages = ["ant-brown.png", "ant-orange.png"];
	for (var i = 0; i < antImages.length; i++) {
		game.load.image('ant' + i, 'assets/' + antImages[i]);
	}
	game.load.image('boulder', 'assets/boulder.png');
	game.load.image('help', 'assets/help.png');
}

function create() {
	lastGameUpdate = game.time.now;
	for (var i = 0; i < GAME_WIDTH; i++) {
		for (var j = 0; j < GAME_HEIGHT; j++) {
			var tile = game.add.sprite(i * tileWidth, j * tileHeight, 'tile');
		}
	}

	players = gameJSON["players"];
	moves = gameJSON["moves"];
	obstacles = gameJSON["obstacles"];

	for (var i = 0; i < obstacles.length; i++) {
		var obstacle = obstacles[i];
		var x = (obstacle["position"])["x"];
		var y = (obstacle["position"])["y"];
		game.add.sprite(x * tileWidth, y * tileHeight, 'boulder');
		console.log("x: " + x + " y: " + y);
	}


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
			ant.callForHelpSprite = game.add.sprite(-1, -1, 'help');
			ant.callForHelpSprite.visible = false;
			ant.callForHelpSprite.anchor.setTo(0.5, 0.5);

			ants.push(ant);
		}
	}
	flag = true;
	displayCurState();
}

var lastGameUpdate;
var currentMoveIndex = -1;
var flag = true;
function update() {
	if (game.time.elapsedSince(lastGameUpdate) > timeSpeed * 1000 && flag) {
		currentMoveIndex++;
		// console.log("index: " + currentMoveIndex);
		if (currentMoveIndex < moves.length) {
			applyMoveToState(moves[currentMoveIndex]);
			displayCurState();
		} else {
			var text;
			if (gameJSON["winnerPlayerID"] === -1)
				text = "Draw!";
			else
				if (gameJSON["winnerPlayerID"] === 0)
					text = myUserName + " Wins!";
				else
					text = selectedAIName + " Wins!";
			console.log(gameJSON["winnerPlayerID"] + ": " +text);
			var style = { font: "60px Arial", fill: "#000000", align: "center" };
			var textSprite = game.add.text(game.world.centerX, game.world.centerY, text, style);
			textSprite.anchor.set(0.5);
			flag = false;
			// TODO: display winner is playerID: gameJSON["winnerPlayerID"]
		}
		lastGameUpdate = game.time.now;
	}

}

// change ants[i]
function applyMoveToState(move) {
	var movedAnts = move["modifiedAnts"];
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
	var antID = move["antID"];
	var callForHelp = move["callForHelp"];
	for (var i = 0; i < ants.length; i++) {
		var ant = ants[i];
		if (ant.data["antID"] === antID) {
			if (callForHelp) {
				ant.callForHelpSprite.visible = true;
			} else {
				ant.callForHelpSprite.visible = false;
			}
		}
	};
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
		var callHelpSprite = ants[i].callForHelpSprite;
		callHelpSprite.x = antSprite.x;
		callHelpSprite.y = antSprite.y;
		setSpriteDir(ants[i].sprite, data["facingDirection"]);
		if (!data["alive"]) {
			antSprite.visible = false;
			ants[i].callForHelpSprite.visible = false;
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
	$("#editor").hide();
	$('#start').hide();
	$("#back").show();
	$("#enemy-selection").hide();
	$("#vsText").show();
	$("#p1Text").text(myUserName);
	$("#p2Text").text(selectedAIName);
	gameJSON = data;
	GAME_WIDTH = (gameJSON["boardDimension"]) ["x"];
	GAME_HEIGHT = (gameJSON["boardDimension"]) ["y"];
	if (game !== null) {
		game.destroy();
		game = null;
	}
	currentMoveIndex = -1;
	game = new Phaser.Game(GAME_WIDTH * tileWidth, GAME_HEIGHT * tileHeight, Phaser.AUTO, 'match', { preload: preload, create: create, update: update });
}

function endGame() {
	$("#editor").show();
	$('#start').show();
	$("#back").hide();
	$("#enemy-selection").show();
	if (game !== null) {
		game.destroy();
		game = null;
	}
}

function runMatch( playerAIs ) {
	$("#background").hide();
	var url = baseUrl + "/match/ai";
	var data = {};
	for (var i = 0; i < playerAIs.length; i++) {
		data["p" + i] = playerAIs[i];
	}

	var request = $.get(url, data,function(data) {
		if (data["luaError"] === null || data["luaError"] === undefined)
			startGame(data);
		else {
			displayError(data);
		}
	}, "json");
	console.log(request);

	request.error(function(jqXHR, textStatus, errorThrown) {
		console.log(errorThrown);
		alert("error: could not connect to server");
	});
}

function displayError(errorData) {
	var text;
	if (errorData["playerID"] !== 0) {
		text = "Error in " + selectedAIName + "'s AI";
	} else {
		text = errorData["luaError"].replace(/\"/g, '');
		text = text.replace(/^\[[^\]]*\]:/, 'Line ');
	}
	$("#errorText").text(text);
	$("#background").show();

}

// USER STUFF

var editor = ace.edit("editor");
editor.setTheme("ace/theme/monokai");
editor.getSession().setMode("ace/mode/lua");
editor.setPrintMarginColumn(-1);
editor.setOption("scrollPastEnd", true);
$("#start").click(function(event) {
	var aiText = editor.getValue();
	saveAI(aiText);
	getSelectedAI(function(AIs) {
		AIs.unshift(aiText);
		runMatch(AIs);
	});
});
loggedOut();

// TODO: add save btn
function saveAI(aiText) {
	var aisRef = fb.child("AIs").child(userAuthData.uid);
	aisRef.set({
		ai: aiText
	});
}

function getSelectedAI(callback) {
	if (selectedAIKey === "self") {
		callback([editor.getValue()]);
	} else {
		var aisRef = fb.child("AIs").child(selectedAIKey);

		aisRef.orderByValue().on("child_added", function(snapshot) {
			callback([snapshot.val()]);
		});
	}
}

function loadMyAI() {
	var aisRef = fb.child("AIs").child(userAuthData.uid);
	editor.setValue("function playTurn ()\n\t--[[Write your AI here--]]\nend");
	aisRef.orderByValue().on("child_added", function(snapshot) {
		editor.setValue(snapshot.val(), 1);
	});
}


var selectedAIName = null;
var selectedAIKey = null;
function loadAIChoices () {
	fb.child("users").orderByKey().on("child_added", function(snapshot) {
		console.log(snapshot.key());
		addUserToList(snapshot.val().name, snapshot.key());
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
		userAuthData = authData;
		loggedIn();
	} else {
		console.log("User is logged out");
		loggedOut();
	}
}

function loggedOut() {
	$("#editor").hide();
	$("#start").hide();
	$("#btn-register").show();
	$("#btn-login").show();
	$("#back").hide();
	$("#panel").hide();
	$("#enemy-selection").hide();
	$("#background").hide();
	$("#vsText").hide();
	userAuthData = null;
	selectedAIName = null;
	selectedAIKey = null;
	isNewUser = false;
	newUserName = null;
	if (game != null) {
		game.destroy();
		game = null;
	}
}

function loggedIn() {
	$("#editor").show();
	$("#start").show();
	$("#btn-register").hide();
	$("#btn-login").hide();
	$("#back").hide();
	$("#panel").show();
	$("#enemy-selection").show();
	$("#background").hide();
	$("#vsText").hide();
	loadAIChoices();
	loadMyAI();
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
	$('#enemy-selection .list').empty();
	enemyOptionKeys = [];
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

var enemyOptionKeys = [];
var myUserName = "";
function addUserToList(userName, key) {
	if (enemyOptionKeys == null || typeof enemyOptionKeys == 'undefined') {
				enemyOptionKeys = [];
	} else {
		for (var i = 0; i < enemyOptionKeys.length; i++) {
			if (enemyOptionKeys[i] === key){
					console.log ("" + enemyOptionKeys[i] + "contains" + key)
					return;
				}
		}
	}
	if (key === userAuthData.uid) {
		myUserName = userName
		userName += " (myself)";
	}
	enemyOptionKeys.push(key);
  	$('#enemy-selection .list').append('<li class="ui-widget-content" data-key="' + key + '">'+userName+'</li>');
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
		},
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

	$("#btn-save").click(function () {
		var aiText = editor.getValue();
		if (aiText !== null)
			saveAI(aiText);
	});

	$("#btn-logout").click(function () {
		logout();
	});

	$("#back").click(function () {
		endGame();
	});

	$("#selectable").selectable({
		autoRefresh: true,
   		selected: function(event, ui) {
        	$(ui.selected).addClass("ui-selected").siblings().removeClass("ui-selected");
   			console.log(ui.selected.getAttribute("data-key"));
   			selectedAIName = ui.selected.textContent;
   			selectedAIKey = ui.selected.getAttribute("data-key");
    	}                   
	});

	// TODO: this doesn't work right.
	function SelectSelectableElement (selectableContainer, elementsToSelect) {
	    $(".ui-selected", selectableContainer).not(elementsToSelect).removeClass("ui-selected").addClass("ui-unselecting");
	    $(elementsToSelect).not(".ui-selected").addClass("ui-selecting");
    	selectableContainer.data("ui-selectable")._mouseStop(null);
	}
	//SelectSelectableElement($("#selectable"), $("li:first", "#selectable"));

});


