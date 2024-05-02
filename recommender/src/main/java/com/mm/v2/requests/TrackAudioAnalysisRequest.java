package com.mm.v2.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.mm.v2.responses.RecommendationResponse;

import com.mm.v2.song.Section;

public class TrackAudioAnalysisRequest {

    String access_token = "BQDcrVcp53RQg1ESjThM2hRHXK7zd-2m71GK-ZgDTXHHHBIflCFTkVhlWvby5zpSi13iBAjg2epajd_CQ_AUAK1N0WHZuRYoF2Z29UHc118_k21P5Kbuv7BLij8ceFIu_I761v6QUqDmFjaDHLf89_478aV3Vch_OA0yRNZ0FFlINjOcwp1nMbJXIRH5uqwVgB2l1VVAlZnMP8w40ndeVu8Hat-XFkk";

    String songID = "1tjqBpO1Y7TzIKp4VlCkHr";
    
    public Section[] getSongAudioAnalysis(String access_token, String song_id)    {

        URL url = null;
        HttpURLConnection con = null;

        Section[] sections = null;

        // first create the URL and open a connection
        try {
            url = new URL("https://api.spotify.com/v1/audio-analysis/" + song_id);
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
            String json = g.toJson(content.toString());
            JsonObject jsonResponse = JsonParser.parseString(content.toString()).getAsJsonObject();
            JsonArray sectionsArray = jsonResponse.getAsJsonArray("sections");

            sections = g.fromJson(sectionsArray, Section[].class);


            // for (Section currSection : sections) {
            //     System.out.println("start: " + currSection.getStart() + " duration: " + currSection.getDuration() + " tempo: " + currSection.getTempo());
            // }

            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sections;
    }

}
