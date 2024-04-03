package com.mm.v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import com.mm.v1.communication.MessageRequestSerializer;
import com.mm.v1.communication.MessageResponseDeserializer;
import com.mm.v1.device.DeviceObject;
import com.mm.v1.requests.AccessTokenRequest;
import com.mm.v1.requests.AddToPlaybackRequest;
import com.mm.v1.requests.AuthorizationRequest;
import com.mm.v1.requests.AvailableDevicesRequest;
import com.mm.v1.requests.RecommendationRequest;
import com.mm.v1.requests.StartPlaybackRequest;
import com.mm.v1.responses.AccessTokenResponse;
import com.mm.v1.responses.AvailableDevicesResponse;
import com.mm.v1.responses.RecommendationResponse;
import com.mm.v1.song.TrackObject;
import com.mm.v3.MessageRequest;

import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD {

    public String access_token;

    private static int wait_duration = 60000 * 30;
    // connection
    private static final int PORT = 5000;
    private static final String HOSTNAME = "172.26.54.24";

    public static void main(String[] args) {
        try {
            new App();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    /**
     * Start the server at :8080 port.
     * @throws IOException
     */
    public App() throws IOException {
        super(8080);

        this.access_token = "";
        // start the server
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");

        AuthorizationDriver auth = new AuthorizationDriver();

        // we want to regenerate an access_token every 30 minutes

        while (true)    {

            System.out.println("*** Authorizing ***");
            auth.authorize();
            System.out.println("Returned from auth driver: Beginning 30 min timer");
    
            try { Thread.sleep(wait_duration); } 
            catch (InterruptedException e) { e.printStackTrace(); }
    
        }

    }

    /**
     * The method that is used as communication with the frontend. There are
     * two main URI endpoints of interest: /plugin and /display
     * 
     * @param session the IHTTPSession object
     * @return the Response object
     */
    @Override
    public Response serve(IHTTPSession session) {

        String uri = session.getUri();
        Map<String, String> params = session.getParms();

        if (uri.equals("/spotify")) {
            // retrieve the authorization code from the redirect 
            String code = params.get("code");
            System.out.println(code);

            AccessTokenResponse resp = new AccessTokenRequest().requestAccessToken(code);
            String access_token = resp.getAccessToken();

            System.out.println("Spotify Access Token: " + access_token);
            this.access_token = access_token;

            // after we get the access token, send it to the second pi
            try (Socket socket = new Socket(HOSTNAME,PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                MessageRequest token_message = new MessageRequest(0, access_token);
                String serialized_message = MessageRequestSerializer.serialize(token_message);
                
                // write the serialized request to the output
                out.println(serialized_message);
                System.out.println("Sent to server: " + serialized_message);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();

            }
        }

        return newFixedLengthResponse(this.access_token);
    }

}