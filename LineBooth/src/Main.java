import LineBoothNXTCommunication.NxtConnection;
import linebooth.ui.MainFrame;

/**
 * IMPORTANT RESOURCES
 * <p/>
 * http://en.wikipedia.org/wiki/Kernel_(image_processing)
 * http://www.jhlabs.com/ip/blurring.html
 * https://developer.apple.com/library/mac/documentation/performance/Conceptual/vImage/Art/kernel_convolution.jpg
 */

public class Main {
    public static void main(String[] args) {
        try {
            LineBoothNXTCommunication.Main test = new LineBoothNXTCommunication.Main();
            test.printTEST();
        }   catch(Exception ex) {
            throw new RuntimeException(ex);
        }


        new MainFrame();
    }
}
