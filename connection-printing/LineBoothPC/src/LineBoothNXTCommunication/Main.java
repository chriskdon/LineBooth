package LineBoothNXTCommunication;

import lejos.pc.comm.NXTCommException;

import java.io.IOException;

public class Main {
    private NxtConnection nxtConnection;

    public byte[][] bike = {
            {0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,1,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0},
            {0,0,0,1,1,1,1,1,0,0,0,1,0,0,0,1,0,0,0,1,1,1,1,1,0,0,0},
            {0,0,1,0,0,0,0,1,1,0,0,1,0,0,0,1,0,0,1,1,0,0,0,0,1,0,0},
            {0,1,0,0,0,0,1,0,0,1,0,0,1,0,1,0,0,1,0,0,1,0,0,0,0,1,0},
            {1,0,0,0,0,0,1,0,0,0,1,0,1,0,1,0,1,0,0,0,1,0,0,0,0,0,1},
            {1,0,0,0,0,1,0,0,0,0,1,0,0,1,0,0,1,0,0,0,0,1,0,0,0,0,1},
            {1,0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,1,0,0,0,0,1,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1},
            {0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,0},
            {0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0},
            {0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0}
    };


    public Main() throws NXTCommException {
        nxtConnection = new NxtConnection("Brain", "001653155151");
    }

    public void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printTEST() throws IOException {
        nxtConnection.sendPrintJob(new PrintJob(PrintJob.FOREGROUND_IMAGE, bike));
//        sleep(5000);
//        nxtConnection.sendPrintJob(new PrintJob(PrintJob.BACKGROUND_IMAGE, tempTESTbackground));
    }

    public static void main(String[] args) throws IOException, NXTCommException {
        Main main = new Main();
        main.printTEST();
    }
}