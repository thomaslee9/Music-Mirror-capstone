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
        // auth.authorize();

        String access_token = "BQCYM4MuqtKV9yFOwnQhRKq3k0TDIY4TlCkkMwzREBtBgjHldhZ3AIc1wefpP9477F9GQcKCrUhtHyEUhAY7pl1jDhOFeyYZgFbUiWNhACqed0KMakm5Z1FD3hOsJQBAU7nSR4YRPBdQPlDXyJ_Vqa60gWHYBchvA97meiaE0vCyFDu8Ev_IMbXhuf59";
        
        SpotifyPlaybackController P = new SpotifyPlaybackController(access_token);

        System.out.println("### Queuing Song ###");

        String song_name = "Losing it";
        String artist_name = "Fisher";

        P.queueSong(song_name, artist_name);

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

            // AvailableDevicesResponse r = new AvailableDevicesRequest().getAvailableDevices(access_token);

            String device_id = "83e2221a51a366dbca4e16114644ef9a6ad165e9";

            System.out.println("### Getting song recommendations ###");

            // StartPlaybackRequest start_playback = new StartPlaybackRequest();
            // start_playback.startPlayback(access_token, device_id);
            
            SeedBuilder builder = new SeedBuilder("3WrFJ7ztbogyGnTHbHJFl2", "rock", "2hOC9qItvmSkgMnxRjgPSr");
            builder.addMinAcousticness("0.1");
            builder.addMinPopularity("75");
            builder.addMaxDanceability("0.5");

            String seed = builder.getSeed();
            System.out.println(seed);

            RecommendationResponse rec = new RecommendationRequest().getSongRecommendation(access_token, seed);

            //for (TrackObject track : rec.getTracks())  {
            //    System.out.println(track.getId());
            //}

            System.out.println("### Adding songs to queue ###");

            String song_id = rec.getTracks()[0].getId();

            System.out.println("### Adding to Queue ###");
            AddToPlaybackRequest p = new AddToPlaybackRequest();
            p.addToQueue(access_token, song_id, device_id);

            System.out.println("### Starting Playback ###");

            StartPlaybackRequest start_playback = new StartPlaybackRequest();
            start_playback.startPlayback(access_token);


        }

        return newFixedLengthResponse("");

    }
}