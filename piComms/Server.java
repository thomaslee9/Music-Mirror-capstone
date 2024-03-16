import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        int portNumber = 18500; // Port number on which the server listens.
        
        try (ServerSocket serverSocket = new ServerSocket(portNumber);
             Socket clientSocket = serverSocket.accept();     
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) { // Read from the client and echo back the input.
                System.out.println("Received from client: " + inputLine);
                out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
