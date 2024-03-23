package com.mirror.lights;

import com.fazecast.jSerialComm.SerialPort;

public class DmxTest {
    public static void main(String[] args) {
        // Local constants

        // Local variables
        DmxJava dmx = new DmxJava();
        byte[] dmxPacket;

        // Specify your COM port name
        String portName = "/dev/cu.usbserial-B001TXFF"; // Adjust this to match your actual port name

        // Get the serial port
        SerialPort comPort = SerialPort.getCommPort(portName);
        comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY); // Set port parameters
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0); // Set timeouts

        // Attempt to open the port
        if (comPort.openPort()) {
            System.out.println("Port opened successfully.");

            // Send one Test DMX Frame
            // dmx.setChannel(1, 100);
            // dmx.setChannel(2, 100);
			// dmxPacket = dmx.render();
			// comPort.writeBytes(dmxPacket, dmxPacket.length);

			// Test Loop
			for (int i = 1; i <= 24; i++) {
			    dmx.setChannel(i, 100);
				dmxPacket = dmx.render();
				comPort.writeBytes(dmxPacket, dmxPacket.length);

				try {
					Thread.sleep(1800); // 50 ms delay, adjust as necessary
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

            // Close the port if done (this part of the code might never be reached in this example)
            // comPort.closePort();
        } else {
            System.out.println("Failed to open port.");
        }
    }
}