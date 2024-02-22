package com.mm.v1.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.gson.Gson;
import com.mm.v1.responses.SearchResponse;

public class AuthorizationRequest {

    public void authorization_request(String access_token, String redirect_uri)  {

        URL url = null;
        HttpURLConnection con = null;
        SearchResponse response = null;

        String q = this.generateQuery(access_token, redirect_uri);

        // first create the URL and open a connection
        try {
            url = new URL("https://accounts.spotify.com/authorize?" + q);
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
            System.out.println(g.toJson(content.toString()));
            response = g.fromJson(content.toString(), SearchResponse.class);
            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //return response;

    }

    private String generateQuery(String client_id, String redirect_uri)  {

        String scope = "user-read-currently-playing user-read-playback-state";

        String body = "client_id=" + client_id + "&" +
                      "response_type=code" + "&" +
                      "redirect_uri=" + redirect_uri;
        
        return body;
    }
    
}
