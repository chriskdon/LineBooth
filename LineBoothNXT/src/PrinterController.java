import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;

import linebooth.image.converters.BitPackedImage;
import linebooth.nxt.PrintJob;

import java.io.IOException;
import java.util.ArrayList;

public class PrinterController {
    private NXTConnection connection;
    ArrayList<PrintJob> printJobQueue;
    private Thread printer;

    public PrinterController() throws IOException {
        connection = USB.waitForConnection();
        printJobQueue = new ArrayList<PrintJob>();
        printer = new Thread(new Printer(this));
    }

    public void run() throws ClassNotFoundException, IOException {
        System.out.println("Start");

        printer.start(); //starts the printer thread

        //while(true) {
        //for(int i=0; i<2; i++) {
        int messageType, numberOfColumns, numberOfRows;

        byte[] messageTypeBytes = new byte[4];
        byte[] numberOfRowsBytes = new byte[4];
        byte[] numberOfColumnsBytes = new byte[4];

        connection.read(messageTypeBytes, 4);
        messageType = byteArrayToInt(messageTypeBytes);

        if(messageType == PrintJob.FOREGROUND_IMAGE || messageType == PrintJob.BACKGROUND_IMAGE) {
            connection.read(numberOfRowsBytes, 4);
            numberOfRows = byteArrayToInt(numberOfRowsBytes);
            connection.read(numberOfColumnsBytes, 4);
            numberOfColumns = byteArrayToInt(numberOfColumnsBytes);

            int imageSize = (int)Math.ceil((double)numberOfColumns*numberOfRows/8);
            byte[] image = new byte[imageSize];
            connection.read(image, imageSize);

//                byte[][] imageMap = new byte[numberOfRows][numberOfColumns];
//                for(int row=0; row<numberOfRows; row++) {
//                    for(int column=0; column<numberOfColumns; column++) {
//                        imageMap[row][column] = (imageBytes[column + numberOfColumns*row]);
//                    }
//                }
            printJobQueue.add(new PrintJob(messageType, new BitPackedImage(numberOfRows, numberOfColumns, image)));

        }

        //}
        //}

        sleep(2000);
    }

    private int byteArrayToInt(byte[] bytes) {
        return (int) ((bytes[3] & 0xff) << 24 ) +
                ((bytes[2] & 0xff) << 16 ) +
                ((bytes[1] & 0xff) << 8 ) +
                ((bytes[0] & 0xff));
    }

    private void sleep(long milli) {
        try {
            Thread.sleep(milli);
        } catch(Exception ex) {

        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        PrinterController main = new PrinterController();
        main.run();
    }
}