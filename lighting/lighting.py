# 18-500 B3
# lighting.py


from dmxpy_lib.dmxpy import DmxPy

# Initialize port
dmx = DmxPy.DmxPy('serial port')

# Set Channel Value
chan = 0
value = 255
dmx.set_channel(chan, value)

# Transmit DMX signals
dmx.render()