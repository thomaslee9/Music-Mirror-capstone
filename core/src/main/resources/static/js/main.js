// main.js

'use strict';

var usernamePage = document.querySelector('#username-page');
var queuePage = document.querySelector('#queue-page');

var usernameForm = document.querySelector('#usernameForm');
var sessionRecButton = document.querySelector('#sessionRecButton');
var songRecButton = document.querySelector('#songRecButton');
var requestForm = document.querySelector('#requestForm');

var requestName = document.querySelector('#reqName');
var requestArtist = document.querySelector('#reqArtist');

var queueArea = document.querySelector('#queueArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    // Set username for New User
    username = document.querySelector('#name').value.trim();

    // Switch Pages if user is logged in
    if (username) {
        // Hide New User Page
        usernamePage.classList.add('hidden');
        // Reveal Queue Page
        queuePage.classList.remove('hidden');

        // WebSocket 
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }

    event.preventDefault();
}

function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);

    stompClient.subscribe('/topic/remove', onVetoReceived);

    // Send JOIN Request
    stompClient.send("/app/queue.addUser", 
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    // Forward Current Queue to New User:
    var userRequest = {
        username: username,
        songName: "NULL",
        songArtist: "NULL",
        type: 'REQUEST'
    };

    stompClient.send("/app/queue.sendRequest", {}, JSON.stringify(userRequest));

    // Hide Connecting... in progress message
    connectingElement.classList.add('hidden');
}

function onError() {
    // Display Connecting Error Message
    connectingElement.computedStyleMap.color = "red";
    connectingElement.textContent = "Could not connect to Music Mirror server. Please try again.";
}

function sendRequest(event) {
    // Parse User Song Request
    var hasName = requestName.value.trim();
    var hasArtist = requestArtist.value.trim();

    if (hasName && hasArtist && stompClient) {
        var userRequest = {
            username: username,
            songName: requestName.value,
            songArtist: requestArtist.value,
            type: 'REQUEST'
        };

        stompClient.send("/app/queue.sendRequest", {}, JSON.stringify(userRequest));
        requestName.value = '';
        requestArtist.value = '';
    }

    event.preventDefault();
}


function sendSessionRequest(event) {

    if (stompClient) {
        var userRequest = {
            username: username,
            songName: '!SESSION_REC',
            songArtist: 'Recommendations based on your listening history',
            type: 'REQUEST'
        };

        stompClient.send("/app/queue.sendRequest", {}, JSON.stringify(userRequest));
        requestName.value = '';
        requestArtist.value = '';
    }

    event.preventDefault();
}

function sendSongRecRequest(event) {
    // Parse User Song Request
    var hasName = requestName.value.trim();
    var hasArtist = requestArtist.value.trim();

    if (hasName && hasArtist && stompClient) {
        var userRequest = {
            username: username,
            songName: requestName.value + ' ' + '!SONG_REC',
            songArtist: requestArtist.value,
            type: 'REQUEST'
        };

        stompClient.send("/app/queue.sendRequest", {}, JSON.stringify(userRequest));
        requestName.value = '';
        requestArtist.value = '';
    }

    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = '-->' + message.username + ' joined the party';

    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = '-->' + message.username + ' left the party';

    } else {
        messageElement.classList.add('chat-message');

        // Debugging Queue Readout
        console.log("Queue: ")
        console.log("NumSongs: ", String(message.queue.length))
        console.log(message);

        // Clear Old Queue
        queueArea.innerHTML = '';

        // Build Queue Elements
        for (var i = 0; i < message.queue.length; i++) {
            // Basic Queue Song View:
            let currSongId = message.queue[i].id;
            var currSongElement = document.createElement('li');
            var currSongText = document.createTextNode(message.queue[i].id + ": '" + message.queue[i].songName + "' by " + message.queue[i].songArtist + " <--- queued by " + message.queue[i].user + " Vote:");
            currSongElement.id = "song_div_" + message.queue[i].id
            currSongElement.style['background-color'] = getAvatarColor(message.queue[i].user);
            currSongElement.appendChild(currSongText);

            // Build the Like button
            var likeButton = document.createElement('button');
            likeButton.id = "like_button_" + currSongId;
            likeButton.style['background-color'] = 'white';
            likeButton.innerHTML = '^';
            likeButton.onclick = function() {
                sendLike(currSongId, 1); // Update backend with new Like
            };
            currSongElement.appendChild(likeButton);

            // Build the Dislike button
            var dislikeButton = document.createElement('button');
            dislikeButton.style['background-color'] = 'white';
            dislikeButton.id = "dislike_button_" + currSongId;
            dislikeButton.innerHTML = 'v';
            dislikeButton.onclick = function() {
                sendLike(currSongId, 2); // Update backend with new Dislike
            };
            currSongElement.appendChild(dislikeButton);

            // Add Song Element to Queue Area
            queueArea.appendChild(currSongElement);
        }
    }

    queueArea.appendChild(messageElement);
    queueArea.scrollTop = queueArea.scrollHeight;
}



function onVetoReceived(payload) {
    // var songId = JSON.parse(payload.body);
    var songId = payload.body;
    // Do nothing if no veto
    if (songId === "none") {
        return;
    }
    var songElementId = "song_div_" + songId;
    // Find the element by its ID
    var songElement = document.getElementById(songElementId);
    // If the element exists, remove it from its parent
    if (songElement) {
        songElement.parentNode.removeChild(songElement);
    }   
    
}

// songID is the spotify song id
// Liked is whether it was liked or not. 0 for nothing 1 for liked 2 for dislike
function sendLike(songId, liked) {
    // If User is connected
    let likeCount = 0;
    if (stompClient) {
        var likeButton = document.querySelector("#like_button_" + songId);
        var dislikeButton = document.querySelector("#dislike_button_" + songId);
        if (liked === 1 && likeButton.style['background-color'] === 'green') {
            likeButton.style['background-color'] = 'white';
            dislikeButton.style['background-color'] = 'white';
            likeCount = 2;
        } else if (liked === 1 && dislikeButton.style['background-color'] === 'red') {
            likeButton.style['background-color'] = 'green';
            dislikeButton.style['background-color'] = 'white';
            likeCount = 3;
        } else if (liked === 1) {
            likeButton.style['background-color'] = 'green';
            dislikeButton.style['background-color'] = 'white';
            likeCount = 1;
        } else if (liked === 2 && dislikeButton.style['background-color'] === 'red') {
            likeButton.style['background-color'] = 'white';
            dislikeButton.style['background-color'] = 'white';
            likeCount = 1;
        } else if (liked === 2 && likeButton.style['background-color'] === 'green') {
            likeButton.style['background-color'] = 'white';
            dislikeButton.style['background-color'] = 'red';
            likeCount = 4;
        } else if (liked === 2) {
            likeButton.style['background-color'] = 'white';
            dislikeButton.style['background-color'] = 'red';
            likeCount = 2;
        }
        // Build User Vote
        var userVote = {
            id: songId,
            username: username,
            vote: likeCount,
            type: 'VOTE'
        };

        // Send to stompClient
        stompClient.send("/app/queue.sendLike", {}, JSON.stringify(userVote));
    }
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

sessionRecButton.addEventListener('click', sendSessionRequest, true);
songRecButton.addEventListener('click', sendSongRecRequest, true)
usernameForm.addEventListener('submit', connect, true)
requestForm.addEventListener('submit', sendRequest, true)