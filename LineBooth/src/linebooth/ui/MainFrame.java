package linebooth.ui;

import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import linebooth.image.GrayscaleBufferedImage;
import linebooth.image.converters.BitPackedImage;
import linebooth.image.converters.BitPacker;
import linebooth.image.converters.GrayImagePacker;
import linebooth.image.extractor.Extractor;
import linebooth.image.extractor.HSVExtractor;
import linebooth.image.filters.*;
import linebooth.image.operations.BinaryOperation;
import linebooth.image.operations.MergeImagesOperation;
import linebooth.nxt.NxtConnection;
import linebooth.nxt.PrintJob;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-30.
 */
public class MainFrame extends JFrame {
    // Supported camera sizes
    private static final Dimension[] CAMERA_SIZES = new Dimension[]{
            new Dimension(25, 25),
            new Dimension(50, 50),
            new Dimension(100, 100)
    };

    private int cameraSizeIndex = 2;

    private ImagePanel outputPanel = new ImagePanel(CAMERA_SIZES[cameraSizeIndex]);
    private ImagePanel packedPanel = new ImagePanel(CAMERA_SIZES[cameraSizeIndex]);

    private JComboBox filterComboBox, backgroundComboBox, dimensionsComboBox;

    private BinaryOperation mergeImages = new MergeImagesOperation();    // Merge images
    private BitPacker converter = new GrayImagePacker();                 // Pack images for printing
    private HSVExtractor foregroundExtractor = new HSVExtractor();          // Extract foreground

    private BufferedImage background;

    public MainFrame() {
        super("LineBooth");

        // Setup Comboboxes
        filterComboBox = new JComboBox(new FilterComboBoxItem[]{
                new FilterComboBoxItem("Foreground", new ForegroundExtractionFilter()),
                new FilterComboBoxItem("None", null),
                new FilterComboBoxItem("Dither", new FloydSteinbergDitherFilter()),
                new FilterComboBoxItem("Winnemoller", new WinnemollerBinarizationFilter(1.2f, 4.2f, 5.25f, 3.9f, 5.5f, 250)),
                new FilterComboBoxItem("Otsu", new OtsuBinarizationFilter()),
                new FilterComboBoxItem("Skin", new SkinFilter())
        });

        backgroundComboBox = new JComboBox(new BackgroundComboBoxItem[]{
                new BackgroundComboBoxItem("None", null),
                new BackgroundComboBoxItem("Hex", getImage("./assets/hexback_320x240.png")),
                new BackgroundComboBoxItem("Mario", getImage("./assets/background_mario.png")),
                new BackgroundComboBoxItem("Swirl", getImage("./assets/background_swirl.png"))
        });

        dimensionsComboBox = new JComboBox(DimensionComboBoxItem.create(CAMERA_SIZES));
        dimensionsComboBox.setSelectedIndex(cameraSizeIndex);
        dimensionsComboBox.addActionListener(new DimensionComboBoxHandler());

        // Image Panel
        add("West", this.outputPanel);
        add("East", this.packedPanel);

        Webcam.getDefault().setCustomViewSizes(CAMERA_SIZES);
        Webcam.getDefault().setViewSize(CAMERA_SIZES[cameraSizeIndex]);
        Webcam.getDefault().addWebcamListener(new WebcamEventHandler());
        Webcam.getDefault().open(true);

        // Controls
        JPanel controlPanel = new JPanel(new GridLayout(7, 2));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        controlPanel.add(new JLabel("Size"));
        controlPanel.add(dimensionsComboBox);

        controlPanel.add(new JLabel("Background"));
        backgroundComboBox.setEnabled(false);
        controlPanel.add(backgroundComboBox);

        controlPanel.add(new JLabel("Filter"));
        controlPanel.add(filterComboBox);

        final JButton backgroundButton = new JButton("Get Background");
        backgroundButton.addActionListener(new GetBackgroundHandler());
        controlPanel.add(backgroundButton);

        JButton printButton = new JButton("Print");
        printButton.addActionListener(new PrinterButtonHandler());
        controlPanel.add(printButton);

        controlPanel.add(new JLabel("Hue"));
        controlPanel.add(new JFloatSlider(0f, 1f, 0.01f, foregroundExtractor.getHueThreshold(), new JFloatSlider.FloatActionEvent() {
            @Override
            public void valueChanged(float value) {
                foregroundExtractor.setHueThreshold(value);
            }
        }));

        controlPanel.add(new JLabel("Saturation"));
        controlPanel.add(new JFloatSlider(0f, 1f, 0.01f, foregroundExtractor.getSaturationThreshold(), new JFloatSlider.FloatActionEvent() {
            @Override
            public void valueChanged(float value) {
                foregroundExtractor.setSaturationThreshold(value);
            }
        }));

        controlPanel.add(new JLabel("Brightness"));
        controlPanel.add(new JFloatSlider(0f, 1f, 0.01f, foregroundExtractor.getBrightnessThreshold(), new JFloatSlider.FloatActionEvent() {
            @Override
            public void valueChanged(float value) {
                foregroundExtractor.setBrightnessThreshold(value);
            }
        }));

        add("South", controlPanel);

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
        setResizable(false);
        setVisible(true);
    }

    public static BufferedImage copyImage(Image im) {
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
            return copyImage(ImageIO.read(new File(path)));
        } catch (Exception ex) {
            throw new RuntimeException("Could not load: " + ex);
        }
    }

    private BufferedImage resizeImage(BufferedImage i, Dimension d) {
        BufferedImage r = new BufferedImage((int) d.getWidth(), (int) d.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = r.createGraphics();
        g.drawImage(i, 0, 0, (int) d.getWidth(), (int) d.getHeight(), null);
        g.dispose();

        return r;
    }

    private BufferedImage calculateImage(BufferedImage image) {
        Filter filter = ((FilterComboBoxItem) filterComboBox.getSelectedItem()).getFilter();
        BufferedImage backgroundOverlay = ((BackgroundComboBoxItem) backgroundComboBox.getSelectedItem()).getBackground();
        BufferedImage output;

        // Add Background
        if (background != null && backgroundOverlay != null) {
            BufferedImage foreground = copyImage(image);

            if (foreground.getWidth() != backgroundOverlay.getWidth() || foreground.getHeight() != backgroundOverlay.getHeight()) {
                backgroundOverlay = resizeImage(backgroundOverlay, ((DimensionComboBoxItem) dimensionsComboBox.getSelectedItem()).getDimension());
            }

            BufferedImage extracted = foregroundExtractor.extract(foreground, background, null);

            output = mergeImages.apply(extracted, backgroundOverlay); // Merge background
        } else {
            output = image;
        }

        // Apply Filter
        if (filter == null) {
            return output;
        } else {
            return filter.apply(output, null);
        }
    }

    private BufferedImage bitPackedToImage(BitPackedImage packedImage) {
        GrayscaleBufferedImage image = new GrayscaleBufferedImage(packedImage.getColumns(),
                packedImage.getRows());

        for (int x = 0; x < packedImage.getColumns(); x++) {
            for (int y = 0; y < packedImage.getRows(); y++) {
                image.setGrayPixel(x, y, (packedImage.getPixel(x, y) == 1 ? 0 : 255));
            }
        }

        return image;
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
            outputPanel.setImage(null);
        }

        @Override
        public void webcamDisposed(WebcamEvent webcamEvent) {
        }

        @Override
        public void webcamImageObtained(WebcamEvent webcamEvent) {
            BufferedImage image = calculateImage(webcamEvent.getImage());

            outputPanel.setImage(image);
            packedPanel.setImage(bitPackedToImage(converter.convert(image)));
        }
    }

    /**
     * Send the image to the NXT Printer
     */
    private class PrinterButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread() { // Send data to the printer
                @Override
                public void run() {
                    BitPackedImage packed = converter.convert(calculateImage(Webcam.getDefault().getImage()));

                    try {
                        NxtConnection connection = new NxtConnection("Brain", "001653155151");
                        connection.sendPrintJob(new PrintJob(PrintJob.FOREGROUND_IMAGE, packed));

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }.start();
        }
    }

    /**
     * Save the baseline background image
     */
    private class GetBackgroundHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            MainFrame.this.background = copyImage(Webcam.getDefault().getImage());
            backgroundComboBox.setEnabled(true);
        }
    }

    /**
     * Used to change the dimesnions on the camera image.
     */
    private class DimensionComboBoxHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            final Dimension d = ((DimensionComboBoxItem)
                    ((JComboBox) actionEvent.getSource()).getSelectedItem()).getDimension();

            // Switch Webcam dimensions
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Webcam.getDefault().close();
                    Webcam.getDefault().setViewSize(d);
                    outputPanel.setSize(d);
                    packedPanel.setSize(d);
                    Webcam.getDefault().open(true);
                }
            }).start();
        }
    }
}
