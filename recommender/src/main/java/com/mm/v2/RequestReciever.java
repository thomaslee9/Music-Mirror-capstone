import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class RequestReceiver extends WebSocketServer {

    public RequestReceiver(InetSocketAddress address) {
        super(address);
        setUpSSL();
    }

    private void setUpSSL() {
        try {
            String STORETYPE = "JKS";
            String KEYSTORE = "../../../../../keystore.jks"; // Path to your keystore file
            String STOREPASSWORD = "b3LukeMattTom"; // Keystore password
            String KEYPASSWORD = "b3LukeMattTom"; // Key password

            KeyStore ks = KeyStore.getInstance(STORETYPE);
            File kf = new File(KEYSTORE);
            ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, KEYPASSWORD.toCharArray());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);

            setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
        conn.send("Welcome to the WebSocket server!");
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
        String host = "0.0.0.0";
        int port = 8080;

        WebSocketServer server = new RequestReceiver(new InetSocketAddress(host, port));
        server.start();
        System.out.println("The WebSocket server started on port: " + server.getPort());
    }
}
