#! /bin/bash

nxjc PrinterController.java Printer.java ../../LineBooth/src/linebooth/image/converters/BitPackedImage.java ../../LineBooth/src/linebooth/nxt/PrintJob.java
nxjlink -cp .:../../LineBooth/src/ -o Printer.nxj PrinterController
nxjupload -r Printer.nxj
