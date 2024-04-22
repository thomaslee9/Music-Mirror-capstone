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

public class DmxTest2 {
    public static void setColor(int chan, int intens, int time, DmxJava dmx, SerialPort comPort) {
        dmx.setChannel(chan, intens);
        byte[] dmxPacket;
        dmxPacket = dmx.render();
        comPort.writeBytes(dmxPacket, dmxPacket.length);

        try {
            Thread.sleep(time); // delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 14; i <= 22; i++) {
            dmx.setChannel(i, 0);
            dmxPacket = dmx.render();
            comPort.writeBytes(dmxPacket, dmxPacket.length);
        }

    }

    public static int randColor() {
        Random rand = new Random();
        return rand.nextInt(5) + 14;
    }

    public static int randColor2() {
        Random rand = new Random();
        return rand.nextInt(3) + 14;
    }

    public static int randColor3() {
        Random rand = new Random();
        return rand.nextInt(3) + 16;
    }

    public static int randIntens() {
        Random rand = new Random();
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

            // Test Model 1
            Random rand = new Random();
            // Setup dimmer
            setColor(13, 128, 50, dmx, comPort);

            // Flash colors
            int chan = randColor();
            int intens = randIntens();
            setColor(chan, intens, 80, dmx, comPort);

            while (true) {
                chan = randColor();
                intens = randIntens();
                setColor(chan, intens, 80, dmx, comPort);
                chan = randColor();
                intens = randIntens();
                setColor(chan, intens, 80, dmx, comPort);
                chan = randColor();
                intens = randIntens();
                setColor(chan, intens, 80, dmx, comPort);
                chan = randColor();
                intens = randIntens();
                setColor(chan, intens, 80, dmx, comPort);
                chan = randColor();
                intens = randIntens();
                setColor(chan, intens, 80, dmx, comPort);
                chan = randColor();
                intens = randIntens();
                setColor(chan, intens, 80, dmx, comPort);
            }
        
            // System.out.println("Finished sequence.");

        } else {

            System.out.println("Failed to open port.");

        }
    }
}
