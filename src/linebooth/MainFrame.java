package linebooth;

import sun.awt.HorizBagLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-30.
 */
public class MainFrame extends DetectCloseFrame {
    public MainFrame(ImageComponent imageComponent) {
        super("LineBooth");

        // Webcam Example --> http://webcam-capture.sarxos.pl/
        this.setLayout(new GridLayout(2, 1));

        // Output
        DetectClosePanel outputPanel = new DetectClosePanel(); // Contains the webcam and the effected imge
        outputPanel.setLayout(new GridLayout());

        outputPanel.add(new ExtendedWebcamPanel());
        outputPanel.add(imageComponent);

        // Controls
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout());

        JSlider test = new JSlider(JSlider.VERTICAL, 0, 100, 0);
        controlPanel.add(test);

        add(outputPanel);
        add(controlPanel);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
