package com.mm.v1.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AddToPlaybackRequest {

    public void addToQueue(String access_token, String song_id, String device_id)   {

        HttpClient client = HttpClient.newHttpClient();

        String url = "";
        String track_uri = "spotify:track:" + song_id;
        try {
            url = "https://api.spotify.com/v1/me/player/queue?uri=" +
                  URLEncoder.encode(track_uri, "UTF-8") +
                  "&device_id=" + device_id;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.noBody())
            .setHeader("Authorization", "Bearer " + access_token)
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Code: " + response.statusCode());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
