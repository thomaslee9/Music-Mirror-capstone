import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class RequestReciever extends WebSocketServer {

    public RequestReciever(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
        conn.send("Welcome to the WebSocket server!"); // Send a welcome message
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from client: " + message);
        conn.send("Echo back: " + message); // Echo the received message back to the client
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
    }

    public static void main(String[] args) {
        String host = "0.0.0.0"; // Bind to all interfaces
        int port = 8080; // Specify your desired port

        WebSocketServer server = new RequestReciever(new InetSocketAddress(host, port));
        server.start();
        System.out.println("The WebSocket server started on port: " + server.getPort());
    }

}
