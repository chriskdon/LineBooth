package linebooth.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-13.
 */
public class ImagePanel extends JPanel {
    private BufferedImage img;

    public ImagePanel(Dimension d) {
        this.setPreferredSize(d);
    }

    /**
     * Change the image being displayed.
     * @param img
     */
    public void setImage(BufferedImage img) {
        this.img = img;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(img != null) {
            g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
        } else {
            g.clearRect(0, 0, this.getWidth(), this.getHeight());
        }
    }
}
