package linebooth;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import linebooth.actions.FloydSteinbergDitherFilter;
import linebooth.actions.WinnemollerBinarization;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-30.
 */
public class MainFrame extends JFrame {
    private Dimension cameraSize = new Dimension(320, 240);

    private ImagePanel imagePanel = new ImagePanel(cameraSize);
    private WinnemollerBinarization binarization = new WinnemollerBinarization(1f, 1.6f, 1.25f, 0.7f, 1.5f, 180);

    private IFilter filter = new FloydSteinbergDitherFilter();

    public MainFrame() {
        super("LineBooth");

        // Webcam Example --> http://webcam-capture.sarxos.pl/
        this.setLayout(new GridLayout(2, 1));

        // Output
        JPanel outputPanel = new JPanel(); // Contains the webcam and the effected imge
        outputPanel.setLayout(new GridLayout());

        Webcam.getDefault().setViewSize(cameraSize);
        Webcam.getDefault().addWebcamListener(new WebcamEventHandler());
        Webcam.getDefault().open(true);

        // outputPanel.add(webcamPanel);
        outputPanel.add(imagePanel);

        // Controls
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout());

        add(outputPanel);
        add(controlPanel);

        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Handle the webcam events.
     */
    private class WebcamEventHandler implements WebcamListener {
        @Override
        public void webcamOpen(WebcamEvent webcamEvent) {

        }

        @Override
        public void webcamClosed(WebcamEvent webcamEvent) {

        }

        @Override
        public void webcamDisposed(WebcamEvent webcamEvent) {

        }

        @Override
        public void webcamImageObtained(WebcamEvent webcamEvent) {
            imagePanel.setImage(filter.apply(webcamEvent.getImage()));

            MainFrame.this.repaint();
        }
    }
}
