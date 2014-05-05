package linebooth.nxt;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import linebooth.image.converters.BitPackedImage;

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

    public void sendForegroundImage(BitPackedImage foregroundImage) throws IOException {
        nxtComm.write(intToByteArray(PrintJob.FOREGROUND_IMAGE));
        sendImage(foregroundImage);
    }

    public void sendBackgroundImage(BitPackedImage backgroundImage) throws IOException {
        nxtComm.write(intToByteArray(PrintJob.BACKGROUND_IMAGE));
        sendImage(backgroundImage);
    }

    public void sendPrintJob(PrintJob printJob) throws IOException {
        nxtComm.write(intToByteArray(printJob.getImageType()));
        sendImage(printJob.getImage());
    }

    private void sendImage(BitPackedImage packedImage) throws IOException {
        nxtComm.write(intToByteArray(packedImage.getRows()));
        nxtComm.write(intToByteArray(packedImage.getColumns()));

        nxtComm.write(packedImage.getPackedImage());
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
