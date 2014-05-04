#! /bin/bash

nxjc PrinterController.java Printer.java PrintJob.java
nxjlink -o Printer.nxj PrinterController
nxjupload -r Printer.nxj
