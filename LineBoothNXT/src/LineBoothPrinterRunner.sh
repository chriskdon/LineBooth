#! /bin/bash

nxjc PrinterController.java Printer.java ../../LineBooth/src/linebooth/image/converters/BitPackedImage.java ../../LineBooth/src/linebooth/nxt/PrintJob.java
if [ $? -ne 0 ]
then
exit -1
fi
nxjlink -cp .:../../LineBooth/src/ -o Printer.nxj PrinterController
if [ $? -ne 0 ]
then
exit -1
fi
nxjupload -r Printer.nxj
