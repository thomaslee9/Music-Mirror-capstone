package com.mm.v1.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.gson.Gson;
import com.mm.v1.responses.AccessTokenResponse;

public class RefreshAccessTokenRequest {

    private String client_id = "7cbd084df6f043f1addef58bc5057f7a";
    private String client_secret = "a5ed8e200d934032b0cfe0f9a824ff5c";

    public AccessTokenResponse requestAccessToken(String refresh_token) {

        URL url = null;
        HttpURLConnection con = null;
        AccessTokenResponse response = null;

        // first create the URL and open a connection
        try {
            url = new URL("https://accounts.spotify.com/api/token");
            con = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String original = this.client_id + ":" + this.client_secret;
        String encoded_auth = "Basic " + Base64.getEncoder().encodeToString(original.getBytes());
        // now we can build the request
        try {
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Authorization", encoded_auth);
            con.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            String data = "grant_type=refresh_token&refresh_token=" + refresh_token;
            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = con.getOutputStream();
            stream.write(out);
                
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
            response = g.fromJson(content.toString(), AccessTokenResponse.class);
            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }

}
