package linebooth;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import linebooth.actions.FloydSteinbergDither;
import linebooth.actions.GrayScaleOutput;
import linebooth.actions.WinnemollerBinarization;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-30.
 */
public class MainFrame extends DetectCloseFrame {
    private Dimension cameraSize = new Dimension(320, 240);

    private ImageComponent imageComponent = new ImageComponent(cameraSize);
    private WinnemollerBinarization binarization = new WinnemollerBinarization(1f, 1.6f, 1.25f, 0.7f, 1.5f, 180);
    private PipelineTransformer<LineBoothState> pipeline;

    public MainFrame() {
        super("LineBooth");

        // Webcam Example --> http://webcam-capture.sarxos.pl/
        this.setLayout(new GridLayout(2, 1));

        // Output
        DetectClosePanel outputPanel = new DetectClosePanel(); // Contains the webcam and the effected imge
        outputPanel.setLayout(new GridLayout());

        pipeline = new PipelineTransformer<LineBoothState>()
                .action(new GrayScaleOutput())             // Change foreground and background to gray
                        //.action(binarization);
                .action(new FloydSteinbergDither());

        Webcam.getDefault().setViewSize(cameraSize);
        Webcam.getDefault().addWebcamListener(new WebcamEventHandler());
        Webcam.getDefault().open(true);

        // outputPanel.add(webcamPanel);
        outputPanel.add(imageComponent);

        // Controls
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout());

        add(outputPanel);
        // add(controlPanel);

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
            imageComponent.setImage(pipeline.result(new LineBoothState(webcamEvent.getImage())).getOutput());

            MainFrame.this.repaint();
        }
    }
}
