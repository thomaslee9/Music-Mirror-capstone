package com.mm.v2;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Random;

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

public class DmxTest {
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

            // Test Pattern 1
            dmx.setChannel(13, 128);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
            System.out.println("Channel: 13");

            int chan = 14;
            int intens = 128;
            Random rand = new Random();
            while (chan <= 22) {

                dmx.setChannel(chan, intens);
                dmxPacket = dmx.render();
                comPort.writeBytes(dmxPacket, dmxPacket.length);
                System.out.println("Channel: " + chan);

                try {
                    Thread.sleep(7000); // delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                chan++;
                // intens = rand.nextInt(255) + 50;
            }

            // System.out.println("Done.");
            // Close the port if done 
            // comPort.closePort();

        } else {
            System.out.println("Failed to open port.");
        }
    }
}