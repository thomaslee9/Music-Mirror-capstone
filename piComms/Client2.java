import java.io.*;
import java.net.*;

public class Client2 {
    public static void main(String[] args) {
        String hostname = "192.168.1.185"; // The IP address of Pi2
        int port = 5000;

        try (Socket socket = new Socket(hostname, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            String message = "Hello from Pi1"; // The message to send
            out.println(message); // Send the message to the server
            out.println(message); // Send the message to the server
            out.println(message); // Send the message to the server
            System.out.println("Sent to server: " + message);
            
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
