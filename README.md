jGPIO - Java GPIO Library for Linux Kernel 3.8
=================================================

This is an entry level GPIO library for Java. I'll be adding onto it rapidly as I get more going, but for now I'm looking for people to test the Device Tree Overlay generator

The Device Tree Overlay is a new introduction in Linux Kernel 3.8 to enforce easier kernel devlopment.

One Parameter is required: -Dgpio_definition=<path to definition file>, this needs topoint at a definition JSON file, see bone.js for an example

Use jGPIO.DTOTest by itself to list the Free GPIO pins

``` java -cp libs/json-simple-1.1.1.jar:./jGPIO.jar -Dgpio_definition=extras/beaglebone.json jGPIO.DTOTest ```

Then you can supply a direct GPIO pin (GPIO2_7 for instance) or a Pin Header P9_41 to generate a DTO file if the pin is free. If the pin isn't free it'll tell you

``` java -cp libs/json-simple-1.1.1.jar:./jGPIO.jar -Dgpio_definition=extras/beaglebone.json jGPIO.DTOTest GPIO2_7 ```

You can supply multiple pins and it will use whichever pins are available to generate the DTO file.

I'm working on the DTO Compilation, firmware installation, loading through the capemgr, and exporting for access via applications.
