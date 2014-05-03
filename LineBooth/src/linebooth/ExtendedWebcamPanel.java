package linebooth;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-30.
 */
public class ExtendedWebcamPanel extends DetectClosePanel {
    public static final Dimension SIZE_320_240 = new Dimension(320, 240);

    private Webcam cam;

    public ExtendedWebcamPanel(Dimension d) {
        cam = Webcam.getDefault();
        cam.setViewSize(d);

        WebcamPanel webcamPanel = new WebcamPanel(cam, d, true);
        this.add(webcamPanel);

        this.setBackground(Color.WHITE);
    }

    public ExtendedWebcamPanel() {
        this(SIZE_320_240);
    }

    public Webcam getWebcam() {
        return this.cam;
    }

    @Override
    public void onClose() {
        cam.close();

        super.onClose();
    }
}
