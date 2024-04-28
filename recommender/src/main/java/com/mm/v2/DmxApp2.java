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

public class DmxApp2 {

    private static String refresh_token = "AQA0oPc9DSdol2r5SxFx3LhHXGylRH4HIevFmjH1605DWojB5jam36ZnluQg34DksHFRv1yOoB0pGOsYRfBUXI1PIyoLeRdGa2TaUE14WUHjupZyE_c2gOvR6RQEMALI7nc";
    private static int MAX_AUTH_TIME_MS = 60000 * 30;
    
        // Local variables
    private static DmxJava dmx = new DmxJava();

    // Specify your COM port name
    private static final String portName = "/dev/ttyUSB0"; // Adjust this to match your actual port name

    // Get the serial port
    private static SerialPort comPort;

    private static int chan = 14;
    
    private static int type = 0;
    
    private static int intens = 80;
    private static int timeDelay = 80;

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
        setColor(13, 128, 50, dmx, comPort);
        
        
        if (!comPort.openPort()) {
            System.out.println("Failed to open port.");
        }
        

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);
            
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


                

                    // now separate control flow based on message_id


                    /*
                     * message_id == 1
                     * 
                     * this message is a song specific recommendation
                     * generate a recommendation using the following fields:
                     * - song_id
                     * - artist_id 
                     * 
                     */
                     
                                         // Run Lights
                                         
                    lightsActive = false;

                    /** -----------GET SPOTIFY SONG ATTRIBUTES------ */
                    SongAudioFeatures features = new TrackAudioFeaturesRequest().getSongAudioFeatures(access_token, song_id);

                    lightsActive = true;
                    float acousticness = Float.parseFloat(features.getAcousticness());
                    float danceability = Float.parseFloat(features.getDanceability());
                    float valence = Float.parseFloat(features.getValence());
                    float energy = Float.parseFloat(features.getEnergy());
                    float tempo = Float.parseFloat(features.getTempo());

                    CompletableFuture.runAsync(() ->runLightsTest(acousticness, danceability, valence, energy, tempo));
               
                    
                } catch (Exception e) {
                    System.out.println("Exception in client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runLightsTest(float acousticness, float danceability, float valence, float energy, float tempo) {
        // Local variables
        byte[] dmxPacket;

        type = getType(acousticness, danceability, valence, energy);

        timeDelay = Math.round(60 / tempo);

        while (lightsActive) {
            // Attempt to open the port
            chan = randColor(type);
            intens = randIntens();
            setColor(chan, intens, timeDelay, dmx, comPort);

        }
    }



    public static void setColor(int chan, int intens, int time, DmxJava dmx, SerialPort comPort) {
        // Transmit serialized DMX frame to lighting fixture
        dmx.setChannel(chan, intens);
        byte[] dmxPacket;
        dmxPacket = dmx.render();
        comPort.writeBytes(dmxPacket, dmxPacket.length);

        // Song BPM Delay
        try {
            Thread.sleep(time); // delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Clear DMX channels
        for (int i = 14; i <= 22; i++) {
            dmx.setChannel(i, 0);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
        }
    }

    public static int getType(float acousticness, float danceability, float valence, float energy) {

        if (acousticness >= 0.5) {
            return 3;
        } else if (danceability >= 0.6 && energy >= 0.6 ) {
            return 0;
        } else if (valence >= 0.6) {
            return 1;
        } else {
            return 2;
        }
    }

    public static int randColor(int type) {
        Random rand = new Random();
        // Generate color based on Song Type
        if (type == 0) {
            // Full Range
            return rand.nextInt(5) + 14;
        } else if (type == 1) {
            // RGB Low Range
            return rand.nextInt(2) + 14;
        } else if (type == 3) {
            // Acoustic Range
            return rand.nextInt(2) + 15;
        } else {
            // Blue Pink/White Red 2 Strobe Hi Range
            return rand.nextInt(3) + 16;
        } 
    }

    public static int randIntens() {
        Random rand = new Random();
        // Generate random intensity within safe range
        return rand.nextInt(225) + 50;
    }
}
