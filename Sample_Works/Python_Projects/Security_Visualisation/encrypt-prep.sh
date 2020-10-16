#!/bin/bash

convert $1 $1.ppm

# split into header and rest 
head -n 3  $1.ppm > $1.header  

tail -n +4  $1.ppm  > $1.bin

# now encrypt with AES and your mode of choice. Use "INFO3616INFO3616" as key.
# YOUR OUTPUT FILE MUST HAVE THE SAME FILENAME AS THE FILE ENDING IN png.bin, BUT WITH AN EXTRA EXTENSION .enc.bin.
# E.g. myfile.png is your chosen input file. After running this script, you have myfile.png.bin.
# The output of your AES code must be a file myfile.png.bin.enc.bin.
