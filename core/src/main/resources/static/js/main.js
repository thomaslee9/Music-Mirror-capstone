// main.js

'use strict';

var usernamePage = document.querySelector('#username-page');
var queuePage = document.querySelector('#queue-page');

var usernameForm = document.querySelector('#usernameForm');
var sessionRecButton = document.querySelector('#sessionRecButton');
var questionButton = document.querySelector('#questionButton');
var songRecButton = document.querySelector('#songRecButton');
var requestForm = document.querySelector('#requestForm');

var requestName = document.querySelector('#reqName');
var requestArtist = document.querySelector('#reqArtist');

var queueArea = document.querySelector('#queueArea');
var connectingElement = document.querySelector('.connecting');

//Today
//var isRefresh = false;

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

//UNCOMMENT
function sendActive() {
    //Get the last user action
    let lastAction = localStorage.getItem('lastAction');
    //Get the last time the server set inactives as a number
    let nextInactive = Number(localStorage.getItem('nextInactive'));
    //Get the current time
    let currentTime = new Date().getTime();

    if (lastAction === null || nextInactive === null) {
        console.warn("Last action or last inactive is null");
        return;
    }

    //If currentTime is greater than the last time the server set inactives then make the new inactive 30 min later
    while (currentTime > nextInactive) {
        localStorage.setItem('nextInactive', nextInactive + 1800000);
        nextInactive = nextInactive + 1800000;
    }

    let lastInactive = nextInactive - 1800000;

    //if last action is less than last time server checked then send an active signal to the server
    if (lastInactive > lastAction) {
        //Send active signal to server
        stompClient.send("/app/userActive", {}, JSON.stringify({ 'userId': userId }));
    }

    //Set last action to now
    localStorage.setItem('lastAction', currentTime);
}

// Reset the timeout whenever the user interacts with the page
window.addEventListener('mousemove', sendActive, true);
window.addEventListener('mousedown', sendActive, true);
window.addEventListener('keydown', sendActive, true);
window.addEventListener('touchmove', sendActive, true);
window.addEventListener('touchstart', sendActive, true);
window.addEventListener('scroll', sendActive, true);


//Today
// function refreshPage(event) {
//     console.log('refreshing page');
//     event.preventDefault();
//     var storedUsername = localStorage.getItem('username');
//     var storedUserId = localStorage.getItem('userId');
//     var storedStompClient = localStorage.getItem('stompClient');
//     console.log("storedUsername: ", storedUsername);
//     console.log("storedUserId: ", storedUserId);
//     console.log("storedStompClient: ", storedStompClient);
//     // If there is, use that information to log the user in
//     if (storedUsername && storedUserId && storedStompClient) {
//         console.warn("User already stored");
//         username = storedUsername;
//         userId = storedUserId;
//         stompClient = storedStompClient;
//         console.log("user stored");
//         // Hide New User Page
//         usernamePage.classList.add('hidden');
//         // Reveal Queue Page
//         queuePage.classList.remove('hidden');
//         connectingElement.classList.add('hidden');

//         // WebSocket
//         if (!stompClient || !stompClient.connected) {
//             console.log("StompClient not connected");
//             var socket = new SockJS('/ws');
//             stompClient = Stomp.over(socket);
//             stompClient.subscribe('/topic/public', onMessageReceived);

//             stompClient.subscribe('/topic/remove', onVetoReceived);
//             // Send JOIN Request
//             stompClient.send("/app/queue.addUser", {}, JSON.stringify({sender: username, type: 'JOIN'}));
//             var userRequest = {
//                 username: username,
//                 songName: "NULL",
//                 songArtist: "NULL",
//                 type: 'CONNECT',
//                 userId: userId
//             };
//             stompClient.send("/app/queue.sendRequest", {}, JSON.stringify(userRequest));  

//         } else {
//             console.log("StompClient already connected");
//         }
//     }
// }


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



function reconnect() {
        stompClient.subscribe('/topic/public', onMessageReceived);

        stompClient.subscribe('/topic/remove', onVetoReceived);
        // Send JOIN Request
        // stompClient.send("/app/queue.addUser", {}, JSON.stringify({username: username, type: 'JOIN'}));
        var userRequest = {
            username: username,
            songName: "NULL",
            songArtist: "NULL",
            type: 'CONNECT',
            userId: userId
        };
        stompClient.send("/app/queue.sendRequest", {}, JSON.stringify(userRequest));  

}

//Today
function reconnectUser() {
    console.log('refreshing page');
    var storedUsername = localStorage.getItem('username');
    var storedUserId = localStorage.getItem('userId');
    console.log("storedUsername: ", storedUsername);
    console.log("storedUserId: ", storedUserId);
    // If there is, use that information to log the user in
    if (storedUsername && storedUserId) {
        console.warn("User already stored");
        username = storedUsername;
        userId = storedUserId;
        // Hide New User Page
        usernamePage.classList.add('hidden');
        // Reveal Queue Page
        queuePage.classList.remove('hidden');
        connectingElement.classList.add('hidden');

         // WebSocket
        console.log("StompClient not connected");
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, reconnect, onError);
    }
}




document.addEventListener('visibilitychange', function() {
    if (document.visibilityState === 'visible') {
        if (localStorage.getItem('userId')) {
            reconnectUser();
        }
    }
});

function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);

    stompClient.subscribe('/topic/remove', onVetoReceived);

    //Generate cleint id
    var clientId = Math.floor(Math.random() * 1000000);
    userId = username + clientId.toString();
    //add stuff to local storage
    localStorage.setItem('username', username);
    localStorage.setItem('userId', userId);
    localStorage.setItem('stompClient', stompClient);
    localStorage.setItem('active', 'true');
    console.log("User ID: ", userId);

    // Send JOIN Request
    stompClient.send("/app/queue.addUser",
        {},
        JSON.stringify({userId: userId, type: 'JOIN'})
    )


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
    event.preventDefault();
    // Parse User Song Request
    console.log("____________________CURRENT TIME: " + formatTimeWithMilliseconds());
    var hasName = requestName.value.trim();
    var hasArtist = requestArtist.value.trim();

    //Check if hasName and hasArtist have bad words in them
    if (isBad(hasName) || isBad(hasArtist)) {
        alert("User input contains bad words. Please try again");
        requestName.value = '';
        requestArtist.value = '';
        return;
    }

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
    } else {
        alert("Please enter both a song and artist name to add to the queue.");
    }

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

function questionAsked(event) {

   event.preventDefault();
   alert("Press 'Queue' with the song name and artist filled in to queue the song. \n\n" +
         "Press the 'Song rec' button with the song and artist filled in to queue a song like the one you inputted. \n\n" +
         "Press the 'Session rec' button to get a reccomendation based on all songs. \n\n" +
         "Once songs are added to the queue you can like and dislike them!" );
}

function formatTimeWithMilliseconds() {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes().toString().padStart(2, '0');
    const seconds = now.getSeconds().toString().padStart(2, '0');
    const milliseconds = now.getMilliseconds().toString().padStart(3, '0');
    return `${hours}:${minutes}:${seconds}.${milliseconds}`;
}

function sendSongRecRequest(event) {
    // Parse User Song Request
    event.preventDefault();
    console.log("____________________CURRENT TIME: " + formatTimeWithMilliseconds());
    var hasName = requestName.value.trim();
    var hasArtist = requestArtist.value.trim();

    //Check if hasName and hasArtist have bad words in them
    if (isBad(hasName) || isBad(hasArtist)) {
        alert("User input contains bad words. Please try again");
        requestName.value = '';
        requestArtist.value = '';
        return;
    }

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
    } else {
        alert("Please enter both a song and artist name to get a song recommendation.");
    }
}

function isBad(input) {
    const badWordRegex = /badword/i;  // Simple pattern to match "badword" anywhere in the string
    return badWordRegex.test(input);
}


function applyScrollingEffect(element) {
    //console.log("text: ", element.textContent);
    //console.log("scrollWidth: ", element.scrollWidth);
    //console.log("clientWidth: ", element.clientWidth);
    if (element.scrollWidth > element.clientWidth) {
        //console.log("scrollingggggggggggg");
        const totalScroll = element.scrollWidth - element.clientWidth;
        element.style.animation = `scroll 10s linear infinite`;
        // Optionally, dynamically adjust the keyframes if static values do not work
        document.styleSheets[0].insertRule(`@keyframes scroll { from { transform: translateX(0%); } to { transform: translateX(-${totalScroll}px); } }`, document.styleSheets[0].cssRules.length);
    } else {
        console.log("ELSEEE");
        element.style.animation = 'none';
    }
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    //var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        let firstTime = Number(message.songName);
        console.log("First Time: ", firstTime);
        while (firstTime < Date.now()) {
            firstTime += 1800000;
        }
        console.log("Next inactive: ", firstTime, " Current Time: ", Date.now());

        localStorage.setItem('nextInactive', firstTime);
        localStorage.setItem('lastAction', Date.now());

    } else if (message.type === 'LEAVE') {
        // messageElement.classList.add('event-message');
        // message.content = '-->' + message.username + ' left the party';

    } else {
        //messageElement.classList.add('chat-message');

        // Debugging Queue Readout
        //console.log("Queue: ")
        //console.log("NumSongs: ", String(message.queue.length))
        //console.log(message);

        // Clear Old Queue
        queueArea.innerHTML = '';

        // Build Queue Elements
        for (var i = 0; i < message.queue.length; i++) {
            // Basic Queue Song View:
            let currQueueId = message.queue[i].queueId;
            let queuedBy = message.queue[i].isRec ? 'DJ Music Mirror' : message.queue[i].username;
            let voteStr = message.queue[i].recComplete ? 'Vote: ' : '';
            //Song that is shown. SongDisplay is what is shown. Strings before make it
            let regularSong = "'" + message.queue[i].songName + "' by " + message.queue[i].songArtist;
            let sessionRec = "Session Recommendation based on your listening history";
            let songRec = "Song similar to " + regularSong;
            let sessionOrSongRec = message.queue[i].songName === "!SESSION_REC" ? sessionRec : songRec;
            let songDisplay = message.queue[i].recComplete ? regularSong : sessionOrSongRec;

            let currSongElement = document.createElement('li');
            let currSongCont = document.createElement('div');
            currSongCont.id = 'scrolling-container';
            let currSongText = document.createElement('div');
            currSongText.className = 'message-content';
            currSongText.textContent = songDisplay + '    Queued by: ' + queuedBy;
            currSongCont.appendChild(currSongText);
            // var queueText = document.createElement('span');
            // queueText.className = 'message-queuedBy';
            // setTimeout(function() {
            //     applyScrollingEffect(queueText);
            // }, 0);
            //queueText.textContent = 'Queued by: ' + queuedBy;
            currSongElement.id = "song_div_" + currQueueId;
            currSongElement.style['background-color'] = message.queue[i].isRec ? DjColor : getAvatarColor(message.queue[i].userId);
            currSongElement.appendChild(currSongCont);
            //currSongElement.appendChild(queueText);
            let likeButtonColor = 'white';
            let dislikeButtonColor = 'white';
            //console.log("color map: ", message.queue[i].colorMap);
            //check if user has clicked the button
            if (message.queue[i].colorMap[userId] !== undefined) {
                if (message.queue[i].colorMap[userId] === 'green') {
                    likeButtonColor = 'green';
                } else if (message.queue[i].colorMap[userId] === 'red') {
                    dislikeButtonColor = 'red';
                }
            }
            // Add buttons if song is loaded
            var btnGroup = document.createElement('div');
            btnGroup.className = "btn-group";
            if (message.queue[i].recComplete === true) {
                // Build the Like button
                var likeButton = document.createElement('button');
                likeButton.id = "like_button_" + currQueueId;
                likeButton.style['background-color'] = likeButtonColor;
                likeButton.className= "fa-solid fa-heart";
                likeButton.onclick = function() {
                    sendLike(currQueueId, 1); // Update backend with new Like
                };
                btnGroup.appendChild(likeButton);

                // Build the Dislike button
                var dislikeButton = document.createElement('button');
                dislikeButton.style['background-color'] = dislikeButtonColor;
                dislikeButton.id = "dislike_button_" + currQueueId;
                dislikeButton.className= "fa-solid fa-thumbs-down";
                dislikeButton.onclick = function() {
                    sendLike(currQueueId, 2); // Update backend with new Dislike
                };
                btnGroup.appendChild(dislikeButton);

            }
            // Add Song Element to Queue Area
            currSongElement.appendChild(btnGroup);
            queueArea.appendChild(currSongElement);
            setTimeout(function() {
                applyScrollingEffect(currSongText);
            }, 0);
           
        }
        console.log("____________________DISPLAY TIME: " + formatTimeWithMilliseconds());
    }

    //queueArea.appendChild(messageElement);
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
questionButton.addEventListener('click', questionAsked, true)
songRecButton.addEventListener('click', sendSongRecRequest, true)
usernameForm.addEventListener('submit', connect, true)
requestForm.addEventListener('submit', sendRequest, true)

window.addEventListener('beforeunload', function() {
   //stompClient.send("/app/userInactive", {}, JSON.stringify({ 'userId': userId }));
   localStorage.removeItem('userId');
});

