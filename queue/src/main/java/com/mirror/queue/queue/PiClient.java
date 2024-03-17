package com.mirror.queue.queue;

import java.io.*;
import java.net.*;

public class PiClient {
    private Socket socket;
    private PrintWriter out;

    // Constructor to establish the connection
    public Client(String hostname, int port) throws IOException {
        // Initialize the socket
        this.socket = new Socket(hostname, port);
        // Initialize the PrintWriter for sending messages to the server
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    // Method to send a message to the server
    public void sendMessage(String message) {
        out.println(message);
        System.out.println("Sent to server: " + message);
    }

    // Close the connection (optional, but recommended)
    public void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException ex) {
            System.out.println("Error closing the connection: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        String hostname = "192.168.200.54"; // The IP address of the server
        int port = 5000;

        try {
            Client client = new Client(hostname, port);
            client.sendMessage("Hello from Pi1"); // Send a message
            // You can send more messages here
            client.closeConnection(); // Close the connection when done
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
