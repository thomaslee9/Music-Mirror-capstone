package com.mm.v2;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Random;

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

public class DmxTest {

    public static int colorID = 2;

    public static void clearAll(DmxJava dmx, SerialPort comPort) {
        byte[] dmxPacket;
        for (int i = 14; i <= 17; i++) {
            dmx.setChannel(i, 0);
            // dmxPacket = dmx.render();
            // comPort.writeBytes(dmxPacket, dmxPacket.length);
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

    public static void setColorTime(int intens, int time, DmxJava dmx, SerialPort comPort) {

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
            Thread.sleep(time); // delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Reset all DMX channels
        // clearAll(dmx, comPort);
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

            int chan = 14;
            int intens = 255;
            int type = 0;
            // timeDelay with 10ms clear channels offset
            int timeDelay = Math.round((60 / tempo) * 1000) - 10;
            timeDelay = 500;
            
            boolean lightsActive = true;

            // Setup dimmer
            setChanTime(13, 128, 50, dmx, comPort);

            clearAll(dmx, comPort);

            while (lightsActive) {

                for (int j = 1; j <= 11; j++) {
                    // Set Color
                    colorID = j;
                    setColorTime(intens, timeDelay, dmx, comPort);

                    // Clear Color
                    colorID = 0;
                    setColorTime(intens, 10, dmx, comPort);
                }
            }

        } else {
            System.out.println("Failed to open port.");
        }
    }
}