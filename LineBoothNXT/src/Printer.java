import lejos.nxt.*;

import linebooth.image.converters.BitPackedImage;
import linebooth.nxt.PrintJob;

public class Printer implements Runnable {
    private static final int PEN_MOVE = 70; // 75
    private static final int PAPER_LENGTH = 1800;
    private static final int PAPER_WIDTH = 1550;

    private static final double factor = 0.09;

    private static final int SEGMENT_HORZ = 110; // 110 -> 7.6v
    private static final int SEGMENT_VERT = 60; // 100 -> 7.6v, 90 -> ?

    private static final int LIGHT_SENSITIVITY = 25;

    private static final NXTRegulatedMotor slideMotor = Motor.B;
    private static final NXTRegulatedMotor paperFeedMotor = Motor.A;
    private static final NXTRegulatedMotor penMotor = Motor.C;
    private static final TouchSensor slideSensor = new TouchSensor(SensorPort.S1);
    private static final ColorSensor paperSensor = new ColorSensor(SensorPort.S3);

    private final PrinterController parent;

    public Printer(PrinterController parent) {
        this.parent = parent;

        slideMotor.setSpeed(1000);
        paperFeedMotor.setSpeed(1000);
        penMotor.setSpeed(1000);
    }

    @Override
    public void run() {
        paperSensor.setFloodlight(false);
        sleep(100);

        while(true) {
            if(Button.readButtons() != 0) { resetPen(); return; }

            if(!parent.printJobQueue.isEmpty()) {
                PrintJob currentPrintJob = parent.printJobQueue.remove(0); //removes the first element

                BitPackedImage image = currentPrintJob.getImage();
                int numberOfRows = currentPrintJob.getImage().getRows();
                int numberOfColumns = currentPrintJob.getImage().getColumns();
                float percentageCompleted = 0;

                switch (currentPrintJob.getImageType()) {
                    case PrintJob.FOREGROUND_IMAGE:

                        resetHorizontal();
                        loadPaper();
                        initPen();
                        break;
                    case PrintJob.BACKGROUND_IMAGE:

                        resetHorizontal(); //reset horizontally
                        down(factor*numberOfRows); //reset vertically
                        initPen();
                        break;
                }

                //Print Image
                for(int y = 0; y < numberOfRows; y++) {
                    for(int x = 0; x < numberOfColumns; x++) {
                        if(image.getPixel(x, y) == 1) {
                            // Next Pixel is also black
                            int skip = 1;
                            x++;
                            while(x < numberOfColumns) {
                                if(image.getPixel(x, y) == 1) {
                                    skip++;
                                    x++;
                                } else {
                                    x--; //to ensure 1 is accounted for in inner loop
                                    break;
                                }
                            }

                            penDown();
                            right(factor*skip);
                            penUp();
                        } else {
                            int skip = 1;
                            x++;
                            while(x < numberOfColumns) {

                                if(image.getPixel(x, y) == 0) {
                                    skip++;
                                    x++;
                                } else {
                                    x--; //to ensure 1 is accounted for in inner loop
                                    break;
                                }
                            }
                            //go to the next row if we are to skip the rest of this row
                            if (x >= numberOfColumns) {
                                break;
                            } else {
                                right(factor*skip);
                            }
                        }

                        if(Button.readButtons() != 0) { resetPen(); return; }

                        LCD.clear();
                        System.out.println("Line: " + (y+1) +"/"+ numberOfRows);
                        System.out.println("Completed: " + Math.round((float)(y+1)/numberOfRows*100) + "%");
                    }

                    resetHorizontal();
                    up(factor);
                }

                resetPen();

                if(currentPrintJob.getImageType() == PrintJob.BACKGROUND_IMAGE) { //eject the page out
                    up(11);
                }

            } else {
                sleep(5000);
            }
        }
    }

    private void sleep(long milli) {
        try {
            Thread.sleep(milli);
        } catch(Exception ex) {

        }
    }

    public int skip(int[] line, int start) {
        //start++;
        int x = 1;
        while(start < line.length && line[start] == 0) {
            x++;

            start++;
        }

        return x;
    }

    // ==========================================
    // CONTROLS
    // ==========================================
    private void right(double i) {
        slideHead((int)(SEGMENT_HORZ * i));
    }

    private void left(double i) {
        slideHead(-1 * (int)(SEGMENT_HORZ * i));
    }

    private void up(double i) {
        feedPaper((int)(SEGMENT_VERT * i));
    }

    private void down(double i) {
        feedPaper(-1 * (int)(SEGMENT_VERT * i));
    }

    private void feedPaper(int length) {
        paperFeedMotor.rotate(length);
    }

    private void slideHead(int length) {
        slideMotor.rotate(length);
    }

    // Get paper to start position
    private void loadPaper() {
        paperFeedMotor.forward();
        while(paperSensor.getLightValue() > LIGHT_SENSITIVITY) { sleep(1); }
        paperFeedMotor.stop();
    }

    // Move to left
    private void resetHorizontal() {
        slideMotor.backward();
        while(!slideSensor.isPressed()){ sleep(1); }
        slideMotor.stop();

        slideMotor.rotate(50);
    }

    private void initPen() {
        penMotor.rotate(-1 * (PEN_MOVE - 20));
    }

    private void resetPen() {
        penMotor.rotate((PEN_MOVE - 20));
    }

    private void penDown() {
        penMotor.rotate(-20);
    }

    private void penUp() {
        penMotor.rotate(20);
    }
}