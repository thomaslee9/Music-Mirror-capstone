package com.mm.v1;

import java.io.IOException;
import java.util.Map;

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

import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD {


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

        // start the server
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");

        AuthorizationDriver auth = new AuthorizationDriver();
        auth.authorize();

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


        }

        return newFixedLengthResponse("");

    }
}