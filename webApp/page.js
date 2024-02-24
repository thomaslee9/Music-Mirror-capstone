const messagesDiv = document.getElementById('messages');
const messageInput = document.getElementById('messageInput');

const raspberry_ip = "192.168.1.185";

const ws = new WebSocket(`ws://${raspberry_ip}:8080`); // Replace <raspberry-pi-ip> with your Raspberry Pi's IP address

ws.onopen = () => {
    console.log('Connected to the WebSocket server');
};

ws.onmessage = (event) => {
    const message = document.createElement('div');
    message.textContent = 'Server: ' + event.data;
    messagesDiv.appendChild(message);
};

ws.onerror = (error) => {
    console.error('WebSocket error:', error);
};

ws.onclose = () => {
    console.log('Disconnected from the WebSocket server');
};

function sendMessage() {
    const message = messageInput.value;
    if (message) {
        ws.send(message);
        messageInput.value = ''; // Clear the input after sending
    }
}
