package linebooth.ui;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class BackgroundComboBoxItem {
    private String text;

    private BufferedImage background;

    public BackgroundComboBoxItem(String text, BufferedImage background) {
        this.text = text;
        this.background = background;
    }

    public BufferedImage getBackground() {
        return background;
    }

    @Override
    public String toString() {
        return text;
    }
}