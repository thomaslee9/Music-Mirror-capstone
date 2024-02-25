package com.mm.v1;

import java.io.IOException;
import java.util.Map;

import com.mm.v1.requests.AccessTokenRequest;
import com.mm.v1.requests.AuthorizationRequest;
import com.mm.v1.requests.RecommendationRequest;
import com.mm.v1.responses.AccessTokenResponse;
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
        // auth.authorize();

        String access_token = "BQDC1xqx11cya-nWjOZOBqYUH-F_3_kz3uoBLzfQ2fZgFJJwrs7tvxtYAubt-UBsurYGMUUkAILANsmda3QW_uAYQgLQN1DzLHs6r7AZZpxMA4AiOJlv374zQCNrFpN7LkYJuKNqq3g9pEZ1NXmChcT0uKIx593POy8rmYbvxk_tgpZtz7qi8MU";

        System.out.println("Spotify Access Token: " + access_token);

        String seed = "seed_artists=4NHQUGzhtTLFvgF5SZesLK&seed_genres=classical%2Ccountry&seed_tracks=0c6xIDDpzE81m2q797ordA";

        RecommendationResponse resp = new RecommendationRequest().getSongRecommendation(access_token, seed);

        for (TrackObject track : resp.getTracks())  {
            System.out.println(track.getName());
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

        }

        return newFixedLengthResponse("");

    }
}