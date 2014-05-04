package linebooth.ui;

import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import linebooth.image.converters.ByteArrayConverter;
import linebooth.image.converters.GrayscaleImageToByteArrayConverter;
import linebooth.image.filters.Filter;
import linebooth.image.filters.FloydSteinbergDitherFilter;
import linebooth.image.filters.OtsuBinarizationFilter;
import linebooth.image.filters.WinnemollerBinarizationFilter;
import linebooth.image.operations.BinaryOperation;
import linebooth.image.operations.MergeImagesOperation;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-30.
 */
public class MainFrame extends JFrame {
    private Dimension cameraSize = new Dimension(320, 240);

    private ImagePanel outputPanel = new ImagePanel(cameraSize);

    private JComboBox<FilterComboBoxItem> filterComboBox;
    private JComboBox<BackgroungComboBoxItem> backgroundComboBox;

    private BinaryOperation mergeImages = new MergeImagesOperation();
    private ByteArrayConverter converter = new GrayscaleImageToByteArrayConverter();

    private BufferedImage output;

    public MainFrame() {
        super("LineBooth");

        // Webcam Example --> http://webcam-capture.sarxos.pl/
        this.setLayout(new GridLayout(2, 1));

        // Output
        JPanel outputPanelBlock = new JPanel();
        outputPanelBlock.setLayout(new GridLayout());

        // Setup Comboboxes
        filterComboBox = new JComboBox<FilterComboBoxItem>(new FilterComboBoxItem[]{
                new FilterComboBoxItem("None", null),
                new FilterComboBoxItem("Dither", new FloydSteinbergDitherFilter()),
                new FilterComboBoxItem("Winnemoller", new WinnemollerBinarizationFilter(1f, 1.6f, 1.25f, 0.7f, 1.5f, 180)),
                new FilterComboBoxItem("Otsu", new OtsuBinarizationFilter()),
        });

        backgroundComboBox = new JComboBox<BackgroungComboBoxItem>(new BackgroungComboBoxItem[]{
                new BackgroungComboBoxItem("None", null),
                new BackgroungComboBoxItem("Hex", getImage("./assets/hexback_320x240.png"))
        });

        // Image Panels
        outputPanelBlock.add(this.outputPanel);

        Webcam.getDefault().setViewSize(cameraSize);
        Webcam.getDefault().addWebcamListener(new WebcamEventHandler());
        Webcam.getDefault().open(true);

        // Controls
        JPanel controlPanel = new JPanel(new GridLayout(3, 2));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        controlPanel.add(new JLabel("Background"));
        controlPanel.add(backgroundComboBox);

        controlPanel.add(new JLabel("Filter"));
        controlPanel.add(filterComboBox);

        controlPanel.add(new JLabel());
        JButton printButton = new JButton("Print");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (output != null) {
                    System.out.println(Arrays.toString(converter.convert(output)));
                }
            }
        });
        controlPanel.add(printButton);

        add(outputPanelBlock);
        add(controlPanel);

        // Handle Quitting on OSX
        Application.getApplication().setQuitHandler(new QuitHandler() {
            @Override
            public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
                Webcam.getDefault().close();
                System.exit(0);
            }
        });

        // Show Window
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bufImg = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
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
            return imageToBufferedImage(ImageIO.read(new File(path)));
        } catch (Exception ex) {
            throw new RuntimeException("Could not load: " + ex);
        }
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
            BufferedImage image = webcamEvent.getImage();

            Filter filter = ((FilterComboBoxItem) filterComboBox.getSelectedItem()).getFilter();
            BufferedImage background = ((BackgroungComboBoxItem) backgroundComboBox.getSelectedItem()).getBackground();

            // Add Background
            if (background != null) {
                output = mergeImages.apply(imageToBufferedImage(image), background);
            } else {
                output = image;
            }

            // Apply Filter
            if (filter == null) {
                outputPanel.setImage(output);
            } else {
                output = filter.apply(output);
                outputPanel.setImage(output);
            }
        }
    }
}
