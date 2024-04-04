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
var userId = null;
var DjColor = '#2196F3';
var colors = [
    '#32c787', '#00BCD4', '#ff5652', '#ffc107', 
    '#ff85af', '#FF9800', '#39bbb0', '#f2274f',
    '#fa11a2', '#cc9a47', '#6ff126', '#cad502',
    '#f111f5', '#6ed00c', '#f602df'
];

let timeoutId;

function resetTimeout() {
    // Clear the existing timeout
    if (timeoutId) {
        clearTimeout(timeoutId);
    }

    // Set a new timeout
    timeoutId = setTimeout(function() {
        // The user has been inactive for 15 minutes
        // Send a message to the server
        stompClient.send("/app/userInactive", {}, JSON.stringify({ 'username': username }));
    }, 1 * 60 * 1000);  // 15 minutes
}

// Reset the timeout whenever the user interacts with the page
window.addEventListener('mousemove', resetTimeout, true);
window.addEventListener('mousedown', resetTimeout, true);
window.addEventListener('keypress', resetTimeout, true);
window.addEventListener('touchmove', resetTimeout, true);

// Set the initial timeout
resetTimeout();

function refreshPage(event) {
    event.preventDefault();
    var storedUsername = localStorage.getItem('username');
    var storedUserId = localStorage.getItem('userId');
    var storedStompClient = localStorage.getItem('stompClient');

    // If there is, use that information to log the user in
    if (storedUsername && storedUserId && storedStompClient) {
        console.warn("User already stored");
        username = storedUsername;
        userId = storedUserId;
        stompClient = storedStompClient; 
        console.log("user stored");
        // Hide New User Page
        usernamePage.classList.add('hidden');
        // Reveal Queue Page
        queuePage.classList.remove('hidden');
        connectingElement.classList.add('hidden');

        // WebSocket 
        if (!stompClient || !stompClient.connected) { 
            var socket = new SockJS('/ws');
            stompClient = Stomp.over(socket);
            stompClient.subscribe('/topic/public', onMessageReceived);

            stompClient.subscribe('/topic/remove', onVetoReceived);
            // Send JOIN Request
            stompClient.send("/app/queue.addUser", {}, JSON.stringify({sender: username, type: 'JOIN'}))   

        }
    }
}


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

    //Generate cleint id
    var clientId = Math.floor(Math.random() * 1000000);
    userId = username + clientId.toString();
    //add stuff to local storage
    localStorage.setItem('username', username);
    localStorage.setItem('userId', userId);
    localStorage.setItem('stompClient', stompClient);
    console.log("User ID: ", userId);

    // Forward Current Queue to New User:
    var userRequest = {
        username: username,
        songName: "NULL",
        songArtist: "NULL",
        type: 'CONNECT',
        userId: userId
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
            type: 'REQUEST',
            userId: userId
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
            type: 'SESSIONREC',
            userId: userId
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
            songName: requestName.value,
            songArtist: requestArtist.value,
            type: 'SONGREC',
            userId: userId
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
            let currQueueId = message.queue[i].queueId;
            let queuedBy = message.queue[i].isRec ? 'DJ Music Mirror' : message.queue[i].username;
            let voteStr = message.queue[i].recComplete ? 'Vote: ' : '';
            //Song that is shown. SongDisplay is what is shown. Strings before make it
            let regularSong = message.queue[i].songName + "' by " + message.queue[i].songArtist;
            let sessionRec = "Session Recommendation based on your listening history";
            let songRec = "Song similar to " + regularSong;
            let sessionOrSongRec = message.queue[i].songName === "!SESSION_REC" ? sessionRec : songRec;
            let songDisplay = message.queue[i].recComplete ? regularSong : sessionOrSongRec;

            var currSongElement = document.createElement('li');
            var currSongText = document.createTextNode(currQueueId + ": '" + songDisplay + " <--- queued by " + queuedBy + voteStr);
            currSongElement.id = "song_div_" + currQueueId;
            currSongElement.style['background-color'] = message.queue[i].isRec ? DjColor : getAvatarColor(message.queue[i].userId);
            currSongElement.appendChild(currSongText);

            let likeButtonColor = 'white';
            let dislikeButtonColor = 'white';
            console.log("color map: ", message.queue[i].colorMap);
            //check if user has clicked the button
            if (message.queue[i].colorMap[userId] !== undefined) {
                if (message.queue[i].colorMap[userId] === 'green') {
                    likeButtonColor = 'green';
                } else if (message.queue[i].colorMap[userId] === 'red') {
                    dislikeButtonColor = 'red';
                }
            }
            // Add buttons if song is loaded
            if (message.queue[i].recComplete === true) {
                // Build the Like button
                var likeButton = document.createElement('button');
                likeButton.id = "like_button_" + currQueueId;
                likeButton.style['background-color'] = likeButtonColor;
                likeButton.innerHTML = '^';
                likeButton.onclick = function() {
                    sendLike(currQueueId, 1); // Update backend with new Like
                };
                currSongElement.appendChild(likeButton);

                // Build the Dislike button
                var dislikeButton = document.createElement('button');
                dislikeButton.style['background-color'] = dislikeButtonColor;
                dislikeButton.id = "dislike_button_" + currQueueId;
                dislikeButton.innerHTML = 'v';
                dislikeButton.onclick = function() {
                    sendLike(currQueueId, 2); // Update backend with new Dislike
                };
                currSongElement.appendChild(dislikeButton);

            }
            // Add Song Element to Queue Area
            queueArea.appendChild(currSongElement);
        }
    }

    queueArea.appendChild(messageElement);
    queueArea.scrollTop = queueArea.scrollHeight;
}



function onVetoReceived(payload) {
    // var queueId = JSON.parse(payload.body);
    var queueId = payload.body;
    // Do nothing if no veto
    if (queueId === "none") {
        return;
    }
    var songElementId = "song_div_" + queueId;
    // Find the element by its ID
    var songElement = document.getElementById(songElementId);
    // If the element exists, remove it from its parent
    if (songElement) {
        songElement.parentNode.removeChild(songElement);
    }   
    
}

// songID is the spotify song id
// Liked is whether it was liked or not. 0 for nothing 1 for liked 2 for dislike
function sendLike(queueId, liked) {
    // If User is connected
    let likeCount = 0;
    if (stompClient) {
        var likeButton = document.querySelector("#like_button_" + queueId);
        var dislikeButton = document.querySelector("#dislike_button_" + queueId);
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
        let color = 'white';
        if (likeButton.style['background-color'] === 'green') {
            color = 'green';
        } else if (dislikeButton.style['background-color'] === 'red') {
            color = 'red';
        }
        var userVote = {
            queueId: queueId,
            username: username,
            vote: likeCount,
            type: 'VOTE',
            userId: userId,
            color: color
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

// document.addEventListener('DOMContentLoaded', refreshPage, true);

// window.addEventListener('unload', function() {
//     localStorage.removeItem('username');
//     localStorage.removeItem('userId');
//     localStorage.removeItem('stompClient');
//     console.log("User Logged Out");
// });

// window.addEventListener('beforeunload', function (e) {
//     // Cancel the event
//     e.preventDefault();
//     // Chrome requires returnValue to be set
//     e.returnValue = '';
// });
