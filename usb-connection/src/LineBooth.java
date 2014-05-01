import lejos.pc.comm.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LineBooth {
    NXTComm nxtComm;


    public LineBooth() {
        try {
            nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
            System.out.println("Successfully made NXTComm object");
        } catch (NXTCommException e) {
            e.printStackTrace();
            System.out.println("Could not make NXTComm object");
        }

//        try {
//            NXTInfo[] infos = nxtComm.search("NXT"); //TODO: THIS IS THE LINE THAT IS BREAKS BLUECOVE ON OSX WHEN BLUETOOTH IS USED (USB CURRENTLY USED)
//
//            for(NXTInfo info: infos) {
//                System.out.println("Name: " + info.name + "; Address: " + info.deviceAddress + "; Protocol: " + info.protocol + "; Connection State: " + getConnectionState(info));
//            }
//        } catch (NXTCommException e) {
//            e.printStackTrace();
//        }

        NXTInfo nxtInfo = new NXTInfo(NXTCommFactory.USB, "NXT", "001653155151");
        System.out.print("\n");
        System.out.println("Name: " + nxtInfo.name + "; Address: " + nxtInfo.deviceAddress + "; Protocol: " + nxtInfo.protocol + "; Connection State: " + getConnectionState(nxtInfo));

        try {
            if(nxtComm.open(nxtInfo)) {
                System.out.println("It was opened.");
                DataOutputStream dataOutputStream = new DataOutputStream(nxtComm.getOutputStream());
                dataOutputStream.writeInt(19);
                dataOutputStream.flush();
                System.out.println("'Twas printed");
            } else {
                System.out.println("It was NOT opened.");
            }
        } catch (NXTCommException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getConnectionState(NXTInfo info) {
        switch (info.connectionState) {
            case PACKET_STREAM_CONNECTED:
                return "PACKET_STREAM_CONNECTED";
            case RAW_STREAM_CONNECTED:
                return "RAW_STREAM_CONNECTED";
            case LCP_CONNECTED:
                return "LCP_CONNECTED";
            case RCONSOLE_CONNECTED:
                return "RCONSOLE_CONNECTED";
            case DATALOG_CONNECTED:
                return "DATALOG_CONNECTED";
            case DISCONNECTED:
                return "DISCONNECTED";
            case UNKNOWN:
                return "UNKNOWN";
            default:
                return null;
        }
    }

    public void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LineBooth lb = new LineBooth();

    }
}
