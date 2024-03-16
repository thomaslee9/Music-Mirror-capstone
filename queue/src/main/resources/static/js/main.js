// main.js

'use strict';

var usernamePage = document.querySelector('#username-page');
var queuePage = document.querySelector('#queue-page');

var usernameForm = document.querySelector('#usernameForm');
var requestForm = document.querySelector('#requestForm');
var requestInput = document.querySelector('#request');
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

    // Send JOIN Request
    stompClient.send("/app/queue.addUser", 
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

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
    var requestContent = requestInput.value.trim();

    if (requestContent && stompClient) {
        var userRequest = {
            username: username,
            songName: requestInput.value,
            type: 'REQUEST'
        };

        stompClient.send("/app/queue.sendRequest", {}, JSON.stringify(userRequest));
        requestInput.value = '';
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

        console.log("queue: ")
        console.log(message);
        console.log("NumSongs: ", String(message.queue.length))

        // Clear Old Queue
        queueArea.innerHTML = '';

        // Build Queue Elements
        for (var i = 0; i < message.queue.length; i++) {
            var currSongElement = document.createElement('li');
            var currSongText = document.createTextNode(message.queue[i].songName);
            currSongElement.appendChild(currSongText);
            queueArea.appendChild(currSongElement);
        }

        // Old User Entry Element
        // var avatarElement = document.createElement('i');
        // var avatarText = document.createTextNode(message.username[0]);
        // avatarElement.appendChild(avatarText);
        // avatarElement.style['background-color'] = getAvatarColor(message.username);

        // messageElement.appendChild(avatarElement);

        // var usernameElement = document.createElement('span');
        // var usernameText = document.createTextNode(message.username);
        // usernameElement.appendChild(usernameText);
        // messageElement.appendChild(usernameElement);
    }

    // Old something else
    // var textElement = document.createElement('p');
    // var messageText = document.createTextNode(message.songName);
    // textElement.appendChild(messageText);

    // messageElement.appendChild(textElement);

    // queueArea.appendChild(messageElement);
    // queueArea.scrollTop = queueArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
requestForm.addEventListener('submit', sendRequest, true)