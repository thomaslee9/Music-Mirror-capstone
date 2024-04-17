package com.mm.v2;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Random;

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
            int chan = 12;
            int intens = 128;
            Random rand = new Random();
            while (true) {
                if (chan > 24) {
                    chan = 12;

                    for (int j = 12; j <= 24; j++) {
                        dmx.setChannel(j, 0);
                        dmxPacket = dmx.render();
                        comPort.writeBytes(dmxPacket, dmxPacket.length);
                    }
                }

                dmx.setChannel(chan, intens);
                dmxPacket = dmx.render();
                comPort.writeBytes(dmxPacket, dmxPacket.length);

                try {
                    Thread.sleep(100); // delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                chan++;
                intens = rand.nextInt(255) + 50;
            }

            // System.out.println("Done.");
            // Close the port if done 
            // comPort.closePort();
        } else {
            System.out.println("Failed to open port.");
        }
    }
}