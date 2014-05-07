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
import linebooth.image.extractor.SubtractionExtractor;
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

    private int cameraSizeIndex = 0;

    private ImagePanel outputPanel = new ImagePanel(CAMERA_SIZES[cameraSizeIndex]);
    private ImagePanel outputPanel2 = new ImagePanel(CAMERA_SIZES[cameraSizeIndex]);
    private JComboBox filterComboBox, backgroundComboBox, dimensionsComboBox;

    private BinaryOperation mergeImages = new MergeImagesOperation();
    private BitPacker converter = new GrayImagePacker();
    private Extractor foregroundExtractor = new SubtractionExtractor();

    private BufferedImage background;

    public MainFrame() {
        super("LineBooth");

        // Webcam Example --> http://webcam-capture.sarxos.pl/
        this.setLayout(new GridLayout(2, 1));

        // Setup Comboboxes
        filterComboBox = new JComboBox(new FilterComboBoxItem[]{
                new FilterComboBoxItem("Foreground", new ForegroundExtractionFilter()),
                new FilterComboBoxItem("None", null),
                new FilterComboBoxItem("Dither", new FloydSteinbergDitherFilter()),
                new FilterComboBoxItem("Winnemoller", new WinnemollerBinarizationFilter(1f, 1.6f, 1.25f, 0.7f, 1.5f, 180)),
                new FilterComboBoxItem("Otsu", new OtsuBinarizationFilter()),
                new FilterComboBoxItem("Skin", new SkinFilter())
        });

        backgroundComboBox = new JComboBox(new BackgroungComboBoxItem[]{
                new BackgroungComboBoxItem("None", null),
                new BackgroungComboBoxItem("Hex", getImage("./assets/hexback_320x240.png"))
        });

        dimensionsComboBox = new JComboBox(DimensionComboBoxItem.create(CAMERA_SIZES));
        dimensionsComboBox.setSelectedIndex(cameraSizeIndex);
        dimensionsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final Dimension d = ((DimensionComboBoxItem)
                        ((JComboBox) actionEvent.getSource()).getSelectedItem()).getDimension();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Webcam.getDefault().close();
                        Webcam.getDefault().setViewSize(d);
                        outputPanel.setSize(d);
                        Webcam.getDefault().open(true);
                    }
                }).run();
            }
        });

        // Image Panel
        add(this.outputPanel);

        System.out.println(outputPanel.getSize());

        Webcam.getDefault().setCustomViewSizes(CAMERA_SIZES);
        Webcam.getDefault().setViewSize(CAMERA_SIZES[cameraSizeIndex]);
        Webcam.getDefault().addWebcamListener(new WebcamEventHandler());
        Webcam.getDefault().open(true);

        // Controls
        JPanel controlPanel = new JPanel(new GridLayout(4, 2));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        controlPanel.add(new JLabel("Size"));
        controlPanel.add(dimensionsComboBox);

        controlPanel.add(new JLabel("Filter"));
        controlPanel.add(filterComboBox);

        controlPanel.add(new JLabel("Background"));
        controlPanel.add(backgroundComboBox);

        final JButton backgroundButton = new JButton("Get Background");
        backgroundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.background = Webcam.getDefault().getImage();
            }
        });
        controlPanel.add(backgroundButton);

        JButton printButton = new JButton("Print");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BitPackedImage packed = converter.convert(calculateImage(Webcam.getDefault().getImage()));

                try {
                    for (int y = 0; y < packed.getRows(); y++) {
                        for (int x = 0; x < packed.getColumns(); x++) {
                           System.out.print(packed.getPixel(x, y));
                        }
                        System.out.println();
                    }

                    NxtConnection connection = new NxtConnection("Brain", "001653155151");
                    connection.sendPrintJob(new PrintJob(PrintJob.FOREGROUND_IMAGE, packed));

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        controlPanel.add(printButton);

        add(controlPanel);

        // Handle Quitting on OSX
        Application.getApplication().setQuitHandler(new QuitHandler() {
            @Override
            public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
                Webcam.getDefault().close();
                System.exit(0);
            }
        });

        add(outputPanel2);

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

    private BufferedImage calculateImage(BufferedImage image) {
        Filter filter = ((FilterComboBoxItem) filterComboBox.getSelectedItem()).getFilter();
        BufferedImage background = ((BackgroungComboBoxItem) backgroundComboBox.getSelectedItem()).getBackground();
        BufferedImage output;

        // Add Background
        if (background != null) {
            output = mergeImages.apply(imageToBufferedImage(image), background);
        } else {
            output = image;
        }

        // Apply Filter
        if (filter == null) {
            return output;
        } else {//if(MainFrame.this.background != null) {
            return filter.apply(output, null);
            //return foregroundExtrator.extract(output, MainFrame.this.background, null);
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
            outputPanel.setImage(null);
        }

        @Override
        public void webcamDisposed(WebcamEvent webcamEvent) {

        }

        @Override
        public void webcamImageObtained(WebcamEvent webcamEvent) {
            BufferedImage image = calculateImage(webcamEvent.getImage());
            BitPackedImage packed = converter.convert(image);
            GrayscaleBufferedImage test = new GrayscaleBufferedImage(packed.getColumns(), packed.getRows());

            for (int y = 0; y < packed.getRows(); y++) {
                for (int x = 0; x < packed.getColumns(); x++) {
                    test.setGrayPixel(x, y, (packed.getPixel(x, y) == 1 ? 0 : 255));
                }
            }

            outputPanel.setImage(image);
            outputPanel2.setImage(test);
        }
    }
}
