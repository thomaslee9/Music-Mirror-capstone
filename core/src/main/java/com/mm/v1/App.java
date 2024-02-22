package com.mm.v1;

import java.io.IOException;
import java.util.Map;

import com.mm.v1.requests.AuthorizationRequest;

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
            System.out.println("Authorization Code: " + code);

        }

        return newFixedLengthResponse("");

    }
}