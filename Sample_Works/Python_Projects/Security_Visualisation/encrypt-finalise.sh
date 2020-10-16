#!/bin/bash

# rename correctly
mv $1.bin.enc.bin $1.enc.bin

# reconstruct
cat $1.header $1.enc.bin > $1.enc.ppm

convert $1.enc.ppm $1.enc.png
