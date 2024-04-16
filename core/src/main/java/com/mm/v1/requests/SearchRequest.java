package com.mm.v1.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.mm.v1.responses.SearchResponse;

public class SearchRequest {

    public SearchResponse searchForTrack(String access_token, 
                                              String song_name, String artist_name,
                                              int encoding_scheme)  {

        URL url = null;
        HttpURLConnection con = null;
        SearchResponse response = null;

        String q = "";
        if (encoding_scheme == 1)   {
            q = this.build_search_query(song_name, artist_name);
        }
        else if (encoding_scheme == 2)  {
            q = this.build_search_query_2(song_name, artist_name);
        }
        // first create the URL and open a connection
        try {
            url = new URL("https://api.spotify.com/v1/search?" + q +
                          "&type=track&limit=3");
            con = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("URL:");
        System.out.println(url.toString());

        // now we can build the request
        try {
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + access_token);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        try {

            int status = con.getResponseCode();
            System.out.println(status);

            BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            
            Gson g = new Gson();
            // System.out.println(g.toJson(content.toString()));
            response = g.fromJson(content.toString(), SearchResponse.class);
            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }

    private String build_search_query(String song_name, String artist_name)  {

        String query = "track:" + song_name + " " +
                       "artist:" + artist_name;

        //System.out.println("Spotify first encoded search query:");
        // System.out.println(q);

        String encoded_q = "q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

        // System.out.println("Spotify final encoded search query:");
        // System.out.println(encoded_q);

        return encoded_q;

    }

    // ex: q=track%3ADoxy%2520artist%3AMiles%2520Davis
    private String build_search_query_2(String song_name, String artist_name)  {

        String query = song_name;
        
        String encoded_q = "q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

        System.out.println("Spotify final encoded search query:");
        System.out.println(encoded_q);

        return encoded_q;

    }
    
}
