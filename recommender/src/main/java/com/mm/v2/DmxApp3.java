package com.mm.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.lang.Thread;
import java.util.concurrent.CompletableFuture;

import com.mm.v2.requests.GetTrackRequest;
import com.mm.v2.requests.TrackAudioFeaturesRequest;
import com.mm.v2.song.SongAudioFeatures;
import com.mm.v2.song.TrackObject;

import com.mm.v2.requests.TrackAudioAnalysisRequest;
import com.mm.v2.song.SongAudioAnalysis;

import com.google.gson.JsonObject;

import com.mm.v2.song.Section;

import org.javatuples.Pair;

import com.mm.v3.MessageRequest;
import com.mm.v3.MessageResponse;
import com.fazecast.jSerialComm.SerialPort;
import com.mm.v2.communication.MessageRequestDeserializer;
import com.mm.v2.communication.MessageResponseSerializer;
import com.mm.v2.requests.RecommendationRequest;
import com.mm.v2.requests.RefreshAccessTokenRequest;
import com.mm.v2.responses.AccessTokenResponse;
import com.mm.v2.responses.RecommendationResponse;
import com.mm.v2.song.TrackObject;


import org.apache.commons.lang3.ArrayUtils;
import java.util.*;


public class DmxApp3 {

    private static String refresh_token = "AQA0oPc9DSdol2r5SxFx3LhHXGylRH4HIevFmjH1605DWojB5jam36ZnluQg34DksHFRv1yOoB0pGOsYRfBUXI1PIyoLeRdGa2TaUE14WUHjupZyE_c2gOvR6RQEMALI7nc";
    private static int MAX_AUTH_TIME_MS = 60000 * 30;
    
    // Local variables
    private static DmxJava dmx = new DmxJava();

    // Specify your COM port name
    private static final String portName = "/dev/cu.usbserial-EN437965"; // Adjust this to match your actual port name

    // Get the serial port
    private static SerialPort comPort;
    
    private static int type = 0;
    private static int intens = 128;
    private static int timeDelay = 80;
    private static int prevColorID = -1;
    // private static int colorID = 1;

    private static boolean lightsActive = false;
    
    private static boolean already = false;
    
    

    public static void main(String[] args) {

        // create the song attribute database
        SongAttributeDatabase db = new SongAttributeDatabase();

        int port = 5001; // The server will listen on this port
        
        comPort = SerialPort.getCommPort(portName);
        comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY); // Set port parameters
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0); // Set timeouts

        // Setup dimmer
        setChanTime(13, 128, 50, dmx, comPort);
        clearAll(dmx, comPort);
        
        if (!comPort.openPort()) {
            System.out.println("Failed to open port.");
        }
        

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            // String access_token = "BQBCmRdj7NYvFCjBE2v0elN3g1HIThFddPxXof7wBJE4f1Rc3jPOkZzZBy4Bm_64Uy29KrcyKeXUzbjSKOK13lqmw6f0pz5izlVrvZMkLjctTceOoYhdP14rWzcQaE4be31uoEW0920d_XBknuc7rHRzEA2N6-ezIaL5IvqAIEe9n9Xi5_bpoWy1VMgJEGJAwr2-PC2hb7g_aQZMjaF8L588IGLrzbM";
            // String songID = "0hKRSZhUGEhKU6aNSPBACZ";
            // SongAudioFeatures features = new TrackAudioFeaturesRequest().getSongAudioFeatures(access_token, songID);
            // Section[] analysis = new TrackAudioAnalysisRequest().getSongAudioAnalysis(access_token, songID);
            // float acousticness = Float.parseFloat(features.getAcousticness());
            // float danceability = Float.parseFloat(features.getDanceability());
            // float valence = Float.parseFloat(features.getValence());
            // float energy = Float.parseFloat(features.getEnergy());
            // float tempo = Float.parseFloat(features.getTempo());
            // lightsActive = true;
            // CompletableFuture.runAsync(() ->runLightsTest2(acousticness, danceability, valence, energy, tempo, analysis));

            
            while (true) {
                
                // accept message from client
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                         
                    
                    // read the message from the first pi
                    String message = in.readLine();
                    System.out.println("Received from client: " + message);
                    MessageRequest request = MessageRequestDeserializer.deserialize(message);
                    String access_token = request.getAccessToken();
                    String song_id = request.getSongId();

                    // Run Lights                  
                    lightsActive = false;

                    /** -----------GET SPOTIFY SONG ATTRIBUTES------ */

                    // Test Spotify ID
                    // String songID = "1tjqBpO1Y7TzIKp4VlCkHr";

                    SongAudioFeatures features = new TrackAudioFeaturesRequest().getSongAudioFeatures(access_token, song_id);
                    Section[] analysis = new TrackAudioAnalysisRequest().getSongAudioAnalysis(access_token, song_id);

                    lightsActive = true;
                    float acousticness = Float.parseFloat(features.getAcousticness());
                    float danceability = Float.parseFloat(features.getDanceability());
                    float valence = Float.parseFloat(features.getValence());
                    float energy = Float.parseFloat(features.getEnergy());
                    float tempo = Float.parseFloat(features.getTempo());

                    CompletableFuture.runAsync(() ->runLightsTest2(acousticness, danceability, valence, energy, tempo, analysis));
               
                    
                } catch (Exception e) {
                    System.out.println("Exception in client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runLightsTest2(float acousticness, float danceability, float valence, float energy, float tempo, Section[] sections) {
        // Local variables
        byte[] dmxPacket;

        // Get type based on Song Attributes
        type = getType(acousticness, danceability, valence, energy);
        // Set timeDelay based on Song Tempo (with 10ms clearAll offset)
        timeDelay = Math.round((60 / tempo) * 1000) - 10;

        // manually set timeDelay
        timeDelay = 490;

        int currSectionID = 0;
        int finalSectionID = sections.length - 1;

        // Start timer
        long startTimeMs = System.currentTimeMillis();
        long nextTime = (long)(sections[1].getStart() * 1000);
        float currTempo = sections[currSectionID].getTempo();
        timeDelay = Math.round((60 / currTempo) * 1000);

        System.out.println(" === Start Song === ");
        System.out.println("Average Tempo: " + tempo + " Start Tempo: " + currTempo);

        while (lightsActive) {
            // Set colorID based on Song Type
            prevColorID = setColorBlock(prevColorID, type, intens, timeDelay, dmx, comPort);

            // If timer expires go to next section
            if (currSectionID < finalSectionID && (System.currentTimeMillis() - startTimeMs) > nextTime) {
                nextTime = (long)(sections[currSectionID + 1].getStart() * 1000);
                currTempo = sections[currSectionID + 1].getTempo();
                timeDelay = Math.round((60 / currTempo) * 1000);  
                currSectionID++;
                System.out.println("New Tempo: " + currTempo);
            }
        }
    }


    public static int setColorBlock(int prevColorID, int type, int intens, int timeDelay, DmxJava dmx, SerialPort comPort) {
        int colorID = randColorID(type, prevColorID);
        // Duration with 10ms clear offset
        int time = timeDelay - 10;
        setColorTime(colorID, intens, time, dmx, comPort);
        // Clear color
        setColorTime(0, intens, 10, dmx, comPort);

        return colorID;
    }

    public static void clearAll(DmxJava dmx, SerialPort comPort) {
        byte[] dmxPacket;
        for (int i = 14; i <= 17; i++) {
            dmx.setChannel(i, 0);
        }
        dmxPacket = dmx.render();
        comPort.writeBytes(dmxPacket, dmxPacket.length);
    }

    public static void setChan(int chan, int intens, DmxJava dmx, SerialPort comPort) {
        byte[] dmxPacket;
        dmx.setChannel(chan, intens);
        dmxPacket = dmx.render();
        comPort.writeBytes(dmxPacket, dmxPacket.length);
    }

    public static void setChanTime(int chan, int intens, int time, DmxJava dmx, SerialPort comPort) {
        // Transmit serialized DMX frame to lighting fixture
        setChan(chan, intens, dmx, comPort);

        // Song BPM Delay
        try {
            Thread.sleep(time); // delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Clear DMX channels
        clearAll(dmx, comPort);
    }

    public static void setColorTime(int colorID, int intens, int timeDelay, DmxJava dmx, SerialPort comPort) {

        byte[] dmxPacket;

        // Set Color based on colorID
        if (colorID == 0) {
            // Off
            clearAll(dmx, comPort);
        } else if (colorID == 1) {
            // White
            dmx.setChannel(14, intens);
            dmx.setChannel(15, intens);
            dmx.setChannel(16, intens);
            dmx.setChannel(17, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("WHITE: " + colorID);
        } else if (colorID == 2) {
            // Red
            dmx.setChannel(14, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("RED: " + colorID);
        } else if (colorID == 3) {
            // Orange
            dmx.setChannel(14, intens);
            dmx.setChannel(17, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("ORANGE: " + colorID);
        } else if (colorID == 4) {
            // Amber
            dmx.setChannel(17, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("AMBER: " + colorID);
        } else if (colorID == 5) {
            // Yellow
            dmx.setChannel(14, intens);
            dmx.setChannel(15, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("YELLOW: " + colorID);
        } else if (colorID == 6) {
            // Lime
            dmx.setChannel(15, intens);
            dmx.setChannel(17, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("LIME: " + colorID);
        } else if (colorID == 7) {
            // Green
            dmx.setChannel(15, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("GREEN: " + colorID);
        } else if (colorID == 8) {
            // Cyan
            dmx.setChannel(15, intens);
            dmx.setChannel(16, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("CYAN: " + colorID);
        } else if (colorID == 9) {
            // Blue
            dmx.setChannel(16, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("BLUE: " + colorID);
        } else if (colorID == 10) {
            // Purple
            dmx.setChannel(14, intens);
            dmx.setChannel(16, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("PURPLE: " + colorID);
        } else if (colorID == 11) {
            // Blue in Red
            dmx.setChannel(16, intens);
            dmx.setChannel(17, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            // System.out.println("BLUinRED: " + colorID);
        }

        // Add Song BPM Delay
        try {
            Thread.sleep(timeDelay); // delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Reset all DMX channels
        // clearAll(dmx, comPort);
    }

    public static int getType(float acousticness, float danceability, float valence, float energy) {
        // Types:
        //      0 - Full Range
        //      1 - Acoustic / Warm
        //      2 - Dance / Disco
        //      3 - Positive / Upbeat
        //      4 - Sad / Moody
        //      5 - Energetic

        if (acousticness >= 0.6 || (acousticness > danceability && acousticness > valence && acousticness > energy)) {
            return 1;
        } else if (danceability >= 0.7 || (danceability > acousticness && danceability > valence && danceability > energy)) {
            return 2;
        } else if (valence >= 0.7 && energy > 0.5) {
            return 3;
        } else if (valence <= 0.3 && energy < 0.5) {
            return 4;
        } else if (energy >= 0.7) {
            return 5;
        } else {
            return 0;
        }
    }

    public static boolean checkList(int[] array, int target) {
        boolean result = false;
        for (int curr: array) {
            if (curr == target) {
                result = true;
            }
        }
        return result;
    }

    public static int randColorID(int type, int prevColorID) {
        // Types:
        //      0 - Full Range
        //      1 - Acoustic / Warm
        //      2 - Dance / Disco
        //      3 - Positive / Upbeat
        //      4 - Sad / Moody
        //      5 - Energetic

        Random rand = new Random();

        // Generate color based on Song Type
        if (type == 0) {
            // Full Range
            int[] colors = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 } ;
            if (checkList(colors, prevColorID)) {
                colors = ArrayUtils.removeElement(colors, prevColorID);
                int index = rand.nextInt(9);
                return colors[index];
            }
            int index = rand.nextInt(10);
            return colors[index];
        } else if (type == 1) {
            // Acoustic / Warm
            int[] colors = new int[]{ 1, 2, 3, 4, 5 };
            if (checkList(colors, prevColorID)) {
                colors = ArrayUtils.removeElement(colors, prevColorID);
                int index = rand.nextInt(3);
                return colors[index];
            }
            int index = rand.nextInt(4);
            return colors[index];
        } else if (type == 2) {
            // Dance / Disco
            int[] colors = new int[]{ 1, 2, 5, 7, 8, 10 };
            if (checkList(colors, prevColorID)) {
                colors = ArrayUtils.removeElement(colors, prevColorID);
                int index = rand.nextInt(4);
                return colors[index];
            }
            int index = rand.nextInt(5);
            return colors[index];
        } else if (type == 3) {
            // Positive / Upbeat
            int[] colors = new int[]{ 3, 5, 6, 8, 10 };
            if (checkList(colors, prevColorID)) {
                colors = ArrayUtils.removeElement(colors, prevColorID);
                int index = rand.nextInt(3);
                return colors[index];
            }
            int index = rand.nextInt(4);
            return colors[index];
        } else if (type == 4) {
            // Sad / Moody
            int[] colors = new int[]{ 1, 7, 8, 9, 10, 11 };
            if (checkList(colors, prevColorID)) {
                colors = ArrayUtils.removeElement(colors, prevColorID);
                int index = rand.nextInt(4);
                return colors[index];
            }
            int index = rand.nextInt(5);
            return colors[index];
        } else {
            // Energetic
            int[] colors = new int[]{ 2, 3, 4, 7, 8, 9 };
            if (checkList(colors, prevColorID)) {
                colors = ArrayUtils.removeElement(colors, prevColorID);
                int index = rand.nextInt(4);
                return colors[index];
            }
            int index = rand.nextInt(5);
            return colors[index];
        } 
    }

    public static int randIntens() {
        Random rand = new Random();
        // Generate random intensity within safe range
        return rand.nextInt(225) + 80;
    }

}
