# STEP 3 Implement a new design for the digital watchface

**Objective:** The designer provided the resources and mockup of the design: we have to change the code to match with the new design. All the necessary resources for this step are in the current folder (`step3`).

## 3.1 Change the layout of the watchface

We will use what we learned in step 1 to lay out the widgets like in the given design:

1. Search for the method `createDigital()` of the class `WatchfacePage`. It is responsible for creating the layout of the digital watchface.
2. Create a new `Canvas` (`ej.widget.container.Canvas`). It is a container from the Widget library which is very convenient because it can lay out widgets at any given coordinates.
3. Add the 5 widgets (heart rate, steps, distance, digital clock and battery) to the canvas. The bounds to use are given in the table below.
4. Make the method `createDigital()` return the canvas.

To speed-up the not-very-exciting process of finding the positions of the widgets within the canvas, we provide here the bounds to use when calling the `Canvas.addChild()` method (x, y, width, height):

- Heart rate: (110, 5, 170, 54)	
- Digital clock: (110, 70, 185, 65)
- Steps: (53, 200, 125, 55)
- Distance: (215, 200, 125, 55)
- Battery: (183, 352, 32, 32)


## 3.2 Change the style of the watchface

We will use what we learned in step 2 to customize the style of the widgets like in the given design:

1. Search for the method `populateStylesheet()` of the class `WatchfacePage`. It is responsible for defining the styles of the digital watchface.
2. Change the existing styles accordingly to match with the design (text colors, background image).
3. In the class `Images`, change the path of the constants `SHOE_ICON` and `LOCALIZATION_ICON` to be the path to the new icons. These constants are used by the shoe and distance widgets of the watchface.

Resources to use:

Shoe icon: `/step3/shoe.png`
Localization icon: `/step3/localization.png`
Background image: `/step3/background.png`
Text color: Black (`0x000000` or `Colors.BLACK`)


---  
_Copyright 2022 MicroEJ Corp. All rights reserved._  
_Use of this source code is governed by a BSD-style license that can be found with this software._
