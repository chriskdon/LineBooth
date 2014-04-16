package linebooth;

import java.awt.image.BufferedImage;

public class LineBoothState {
    private BufferedImage background, foreground, output;

    public LineBoothState(BufferedImage b, BufferedImage f) {
        if(b.getWidth() != f.getWidth() || b.getHeight() != f.getHeight()) {
            throw new IllegalArgumentException("Image dimensions must be the same.");
        }

        this.background = b;
        this.foreground = f;

        this.output = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    public BufferedImage getBackground() {
        return background;
    }

    public BufferedImage getForeground() {
        return foreground;
    }

    public BufferedImage getOutput() {
        return output;
    }

    public void setOutput(BufferedImage img) {
        this.output = img;
    }

    public int getWidth() {
        return background.getWidth();
    }

    public int getHeight() {
        return background.getHeight();
    }
}