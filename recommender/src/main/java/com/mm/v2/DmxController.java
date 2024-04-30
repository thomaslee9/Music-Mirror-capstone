package com.mm.v2;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

// 10-CH @ d 13

// Channel Mapping for SlimPAR Pro Q USB Lighting Fixture
// 13 - dimmer setup
// 14 - red
// 15 - green
// 16 - blue
// 17 - amber
// 18 - color macros
// 19 - strobe
// 20 - sound-active
// 21 - auto rotate colors
// 22 - dimmer speed mode

// Color Mapping                    ID:
// Off - NONE                       0
// White - 14 & 15 & 16 & 17        1
// Red - 14                         2
// Orange - 14 & 17                 3
// Amber - 17                       4
// Yellow - 14 & 15                 5
// Lime - 15 & 17                   6
// Green - 15                       7
// Cyan - 15 & 16                   8
// Blue - 16                        9
// Purple - 14 & 16                 10
// Blue in Red - 16 & 17            11

public class DmxController {

    // public static int colorID = 2;

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
            System.out.println("WHITE: " + colorID);
        } else if (colorID == 2) {
            // Red
            dmx.setChannel(14, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("RED: " + colorID);
        } else if (colorID == 3) {
            // Orange
            dmx.setChannel(14, intens);
            dmx.setChannel(17, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("ORANGE: " + colorID);
        } else if (colorID == 4) {
            // Amber
            dmx.setChannel(17, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("AMBER: " + colorID);
        } else if (colorID == 5) {
            // Yellow
            dmx.setChannel(14, intens);
            dmx.setChannel(15, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("YELLOW: " + colorID);
        } else if (colorID == 6) {
            // Lime
            dmx.setChannel(15, intens);
            dmx.setChannel(17, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("LIME: " + colorID);
        } else if (colorID == 7) {
            // Green
            dmx.setChannel(15, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("GREEN: " + colorID);
        } else if (colorID == 8) {
            // Cyan
            dmx.setChannel(15, intens);
            dmx.setChannel(16, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("CYAN: " + colorID);
        } else if (colorID == 9) {
            // Blue
            dmx.setChannel(16, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("BLUE: " + colorID);
        } else if (colorID == 10) {
            // Purple
            dmx.setChannel(14, intens);
            dmx.setChannel(16, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("PURPLE: " + colorID);
        } else if (colorID == 11) {
            // Blue in Red
            dmx.setChannel(16, intens);
            dmx.setChannel(17, intens);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("BLUinRED: " + colorID);
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

    public static void main(String[] args) {

        // Local variables
        DmxJava dmx = new DmxJava();
        byte[] dmxPacket;

        // Specify your COM port name
        String portName = "/dev/cu.usbserial-EN437965"; // Adjust this to match your actual port name

        // Get the serial port
        SerialPort comPort = SerialPort.getCommPort(portName);
        comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY); // Set port parameters
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0); // Set timeouts

        // Attempt to open the port
        if (comPort.openPort()) {
            System.out.println("Port opened successfully.");

            // Test Pattern 
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);

            // Spotify Song Attributes
            float acousticness = 0;
            float danceability = 0;
            float valence = 0;
            float energy = 0;
            float tempo = 122;

            int prevColorID = -1;
            int colorID = 1;
            int intens = 255;
            int type = 0;

            // timeDelay with 10ms clear channels offset
            int timeDelay = Math.round((60 / tempo) * 1000) - 10;

            // Manually set timeDelay
            timeDelay = 490;
            
            boolean lightsActive = true;

            // Setup dimmer
            setChanTime(13, 128, 50, dmx, comPort);
            clearAll(dmx, comPort);

            // Set type based on Song Attributes
            type = getType(acousticness, danceability, valence, energy);

            // Manual Type
            type = 0;

            while (lightsActive) {
                // Set colorID based on Song type
                colorID = randColorID(type, prevColorID);
                setColorTime(colorID, intens, timeDelay, dmx, comPort);
                prevColorID = colorID;

                // Clear color
                setColorTime(0, intens, 10, dmx, comPort);
            }

        } else {
            System.out.println("Failed to open port.");
        }
    }
}
