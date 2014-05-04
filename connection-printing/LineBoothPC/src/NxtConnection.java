import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

import java.io.IOException;

public class NxtConnection {
    private NXTComm nxtComm; //connection handler to NXT
    private NXTInfo nxtInfo; //information on the NXT controller to connect to


    public NxtConnection(String nxtName, String nxtAddress) throws NXTCommException {
        this.nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
        this.nxtInfo = new NXTInfo(NXTCommFactory.USB, nxtName, nxtAddress);

        if(!nxtComm.open(nxtInfo)) {
            throw new NXTCommException("Error: Could not open connection to specified NXT device");
        }
    }

    public void sendForegroundImage(byte[][] foregroundImage) throws IOException {
        nxtComm.write(intToByteArray(PrintJob.FOREGROUND_IMAGE));
        sendImage(foregroundImage);
    }

    public void sendBackgroundImage(byte[][] backgroundImage) throws IOException {
        nxtComm.write(intToByteArray(PrintJob.BACKGROUND_IMAGE));
        sendImage(backgroundImage);
    }

    public void sendPrintJob(PrintJob printJob) throws IOException {
        nxtComm.write(intToByteArray(printJob.getImageType()));
        sendImage(printJob.getImage());
    }

    private void sendImage(byte[][] image) throws IOException {
        int rowLength = image.length;
        int columnLength = image[0].length;

        nxtComm.write(intToByteArray(rowLength));
        nxtComm.write(intToByteArray(columnLength));

        byte[] imageBytes = new byte[rowLength * columnLength];
        for(int row=0; row<rowLength; row++) {
            for(int column=0; column<columnLength; column++) {

                imageBytes[column + columnLength*row] = image[row][column];
                //System.out.print(imageBytes[column + columnLength*row] + " ");
            }
        }
        nxtComm.write(imageBytes);
    }

    private byte[] intToByteArray(int integer) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte)(integer >>> (i * 8));
            //System.out.print(bytes[i] + " , ");
        }
        return bytes;
    }
}
