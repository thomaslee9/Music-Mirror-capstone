import argparse
import time

import serial
from serial.tools import list_ports


def grep_ports(grep):
    ports = list(list_ports.grep(grep))
    assert len(ports) == 1, 'more than 1 port matching %s' % grep
    port = ports[0]
    return port.device


class DmxPy:
    def __init__(self, serial_port=None, baud_rate=57600, default_level=255,
                 dmx_size=512, levels=255, port_grep='0403:6001'):
        self.default_level = default_level
        self.levels = levels
        self.dmx_size = dmx_size
        if serial_port is None:
            serial_port = grep_ports(port_grep)
            print('Found DMX device matching %s at' % port_grep, serial_port)
        try:
            self.serial = serial.Serial(serial_port, baudrate=baud_rate)
        except serial.SerialException:
            print('Error: could not open Serial Port:', serial_port)
            exit(1)
        if self.default_level < 0 or self.default_level > self.levels:
            print('Light level must be 0-%d, not:' % self.levels,
                  self.default_level)
            exit(1)
        self.dmxData = [0] + [self.default_level] * self.dmx_size

    def set_channel(self, channel, level):
        channel = max(1, min(channel, self.dmx_size))  # clamp 1-self.dmx_size
        level = max(0, min(level, 255))  # clamp 0-255
        self.dmxData[channel] = level

    def blackout(self):
        self.dmxData = [0] + [0] * self.dmx_size

    def whiteout(self):
        self.dmxData = [0] + [self.default_level] * self.dmx_size

    def render(self):
        dmx_open = [0x7E]
        dmx_close = [0xE7]
        dmx_intensity = [6, (self.dmx_size + 1) & 0xFF,
                         (self.dmx_size + 1) >> 8 & 0xFF]
        self.serial.write(
            bytearray(dmx_open + dmx_intensity + self.dmxData + dmx_close))


def main():
    parser = argparse.ArgumentParser(description='Control Enttec DMX USB Pro')
    parser.add_argument('-r', '--rate', type=int, default=57600,
                        help='baud rate for USB communication '
                             '(default: 57600)')
    parser.add_argument('-p', '--port', type=str,
                        help='Serial(COM) port, e.g., /dev/ttyUSB1 or COM3')
    parser.add_argument('-g', '--port-grep', type=str, default='0403:6001',
                        help='if port not specified attempt to auto-detect '
                             'serial matching grep (default: 0403:6001)')
    parser.add_argument('-l', '--level', type=int, default=255,
                        choices=range(0, 256),
                        help='default level [0-255] of unspecified channels '
                             '(default: 255)')
    parser.add_argument('-s', '--size', type=int, default=512,
                        help='DMX Size (default: 512)')
    parser.add_argument('-b', '--blackout', action='store_true',
                        help='Turn off all lights (level=0)')
    parser.add_argument('-w', '--whiteout', action='store_true',
                        help='Turn on all lights at level')
    parser.add_argument('-d', '--demo', action='store_true',
                        help='Play demo light pattern')
    args = parser.parse_args()

    dmx = DmxPy(args.port, baud_rate=args.rate, default_level=args.level,
                dmx_size=args.size, port_grep=args.port_grep)
    if not args.blackout and not args.whiteout and not args.demo:
        print('Select an action: [b]lackout, [w]hiteout, or [d]emo')
        parser.print_usage()
        exit(1)

    if args.blackout:
        dmx.blackout()
        dmx.render()
    elif args.whiteout:
        dmx.whiteout()
        dmx.render()
    elif args.demo:
        dmx.set_channel(1, 100)
        dmx.set_channel(2, 50)
        dmx.render()
        time.sleep(5)
        dmx.set_channel(3, 100)
        dmx.render()
        time.sleep(5)
        dmx.blackout()
        dmx.render()
        time.sleep(5)
        dmx.whiteout()
        dmx.render()
