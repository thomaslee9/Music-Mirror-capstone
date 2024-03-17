package com.mm.v1.queue;

import java.io.*;
import java.net.*;

public class PiClient {
    private Socket socket;
    private PrintWriter out;

    // Constructor to establish the connection
    public PiClient(String hostname, int port) throws IOException {
        try {
            // Initialize the socket
            this.socket = new Socket(hostname, port);
            // Initialize the PrintWriter for sending messages to the server
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    // Method to send a message to the server
    public void sendMessage(String message) throws IOException{
        
        String hostname = "192.168.1.185"; // The IP address of the server
        int port = 5000;
        this.socket = new Socket(hostname, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        
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
        String hostname = "192.168.1.185"; // The IP address of the server
        int port = 5000;

        try {
            PiClient client = new PiClient(hostname, port);
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
