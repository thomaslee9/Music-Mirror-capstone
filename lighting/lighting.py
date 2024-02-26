# 18-500 B3
# lighting.py


from PyDMXControl.controllers import OpenDMXController
from PyDMXControl.profiles.Generic import Dimmer
from PyDMXControl import Colors

# Basic Colors
Black = [000, 000, 000, 000]
White = [255, 255, 255, 255]
Warm = [255, 170, 85, 85]
Red = [255, 000, 000, 000]
Amber = [255, 127, 000, 000]
Yellow = [255, 255, 000, 000]
Green = [000, 255, 000, 000]
Cyan = [000, 255, 255, 000]
Blue = [000, 000, 255, 000]
Pink = [255, 105, 180, 000]
# UV = [75, 000, 130, 000]
Magenta = [255, 000, 255, 000]

# Transmit on DMX signal line
dmx = OpenDMXController()

# Instantiate main lighting fixture
fixture = dmx.add_fixture(Dimmer, name="Fixture_1")

fixture.color(Cyan, 500)

fixture.off()
fixture.on()

fixture.off()
fixture.on()

fixture.off()
fixture.on()

fixture.color(Amber, 2000)

fixture.dim(255, 5000)


# Web Control Panel
dmx.web_control()

# Debug Control
dmx.debug_control()

# Sleep until ENTER console interrupt
dmx.sleep_till_enter()

# End DMX output
dmx.close()
