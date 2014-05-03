package linebooth;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.ds.cgt.WebcamCloseTask;
import linebooth.actions.GrayScaleForegroundBackground;
import linebooth.actions.WinnemollerBinarization;
import sun.awt.HorizBagLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-30.
 */
public class MainFrame extends DetectCloseFrame {
    private ImageComponent imageComponent = new ImageComponent(new Dimension(320, 240));
    private WinnemollerBinarization binarization = new WinnemollerBinarization(1f, 1.6f, 1.25f, 0.7f, 1.5f, 180);
    private PipelineTransformer<LineBoothState> pipeline;

    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bufImg = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics graph = bufImg.getGraphics();
        graph.drawImage(im, 0, 0, null);
        graph.dispose();

        return bufImg;
    }

    /**
     * Get an image from a file.
     *
     * @param path
     * @return
     */
    public static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception ex) {
            throw new RuntimeException("Could not load: " + path);
        }
    }

   private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public MainFrame() {
        super("LineBooth");

        // Webcam Example --> http://webcam-capture.sarxos.pl/
        this.setLayout(new GridLayout(2, 1));

        // Output
        DetectClosePanel outputPanel = new DetectClosePanel(); // Contains the webcam and the effected imge
        outputPanel.setLayout(new GridLayout());

        //final ExtendedWebcamPanel webcamPanel = new ExtendedWebcamPanel();

        pipeline = new PipelineTransformer<LineBoothState>()
                .action(new GrayScaleForegroundBackground())             // Change foreground and background to gray
                .action(binarization);

        //imageComponent.setImage(pipeline.result(new LineBoothState(deepCopy(webcamPanel.getWebcam().getImage()))).getOutput());

        Webcam.getDefault().setViewSize(new Dimension(320, 240));
        Webcam.getDefault().addWebcamListener(new WebcamListener() {
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
                imageComponent.setImage(pipeline.result(new LineBoothState(deepCopy(webcamEvent.getImage()))).getOutput());

                MainFrame.this.repaint();
            }
        });
        Webcam.getDefault().open(true);

       // outputPanel.add(webcamPanel);
        outputPanel.add(imageComponent);

        // Controls
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout());

        JSlider scaleSlider = new JSlider(JSlider.VERTICAL, 1, 5, 1);
        scaleSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider sender = (JSlider)e.getSource();

                if(!sender.getValueIsAdjusting()) {
                    binarization.setScale(sender.getValue());
                }
            }
        });

        controlPanel.add(scaleSlider);

        add(outputPanel);
        add(controlPanel);

        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
