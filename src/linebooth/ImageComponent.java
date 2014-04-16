package linebooth;

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
public class ImageComponent extends JComponent {
    private BufferedImage img;

    public ImageComponent(String path) {
        super();

        setImage(path);
    }

    public ImageComponent() {
        super();
    }

    /**
     * Change the image being displayed.
     * @param img
     */
    public void setImage(BufferedImage img) {
        this.img = img;

        this.updateUI();
    }

    public Graphics2D getGraphicsContext() {
        return getImage().createGraphics();
    }

    public BufferedImage getImage() {
        if(img == null) {
            throw new RuntimeException("No Image Set");
        }

        return img;
    }

    /**
     * Set the image to the path;
     * @param path
     */
    public void setImage(String path) {
        try {
            setImage(ImageIO.read(new File(path)));
        } catch (IOException e) {
        }
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());

        if(img != null) {
            g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
        }
    }
}
