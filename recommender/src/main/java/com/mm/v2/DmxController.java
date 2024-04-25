package com.mm.v2;

import java.util.Random;

import com.fazecast.jSerialComm.SerialPort;

// Channel Mapping for SlimPAR Pro Q USB Lighting Fixture
// 13 - setup
// 14 - red
// 15 - green
// 16 - blue
// 17 - pink white
// 18 - red 2
// 19 - strobe
// 20 - 
// 21 - auto rotate colors
// 22 - 

public class DmxController {

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

    public static int randColor(int type) {
        Random rand = new Random();
        // Generate color based on Song Type
        if (type == 0) {
            return rand.nextInt(5) + 14;
        } else if (type == 1) {
            return rand.nextInt(3) + 14;
        } else {
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
        String portName = "/dev/cu.usbserial-EN379386"; // Adjust this to match your actual port name

        // Get the serial port
        SerialPort comPort = SerialPort.getCommPort(portName);
        comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY); // Set port parameters
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0); // Set timeouts

        // Attempt to open the port
        if (comPort.openPort()) {
            System.out.println("Port opened successfully.");

            // ======== DMX Controller ======== 

            // Initialize randomizer
            Random rand = new Random();

            // Setup dimmer
            setColor(13, 128, 50, dmx, comPort);

            // Strobe Variables
            int type = 0;
            int bpm = 122;
            int timeUnit = (60 / bpm) * 100;

            // Compute Channel & Intensity for Next DMX Frame
            int chan = randColor(type);
            int intens = randIntens();
            setColor(chan, intens, 80, dmx, comPort);

            while (true) {
                // Compute Channel & Intensity for Next DMX Frame
                chan = randColor(type);
                intens = randIntens();
                setColor(chan, intens, 80, dmx, comPort);
            }
        
            // System.out.println("Finished sequence.");

        } else {
            System.out.println("Failed to open port.");
        }
    }
}
