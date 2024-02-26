package com.mm.v1.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.gson.Gson;
import com.mm.v1.responses.AvailableDevicesResponse;
import com.mm.v1.responses.RecommendationResponse;

public class AvailableDevicesRequest {

    public AvailableDevicesResponse getAvailableDevices(String access_token)    {

        URL url = null;
        HttpURLConnection con = null;
        AvailableDevicesResponse response = null;

        // first create the URL and open a connection
        try {
            url = new URL("https://api.spotify.com/v1/me/player/devices");
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
            response = g.fromJson(content.toString(), AvailableDevicesResponse.class);
            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }

}
