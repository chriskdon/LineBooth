package linebooth;

import java.awt.image.BufferedImage;

public class LineBoothState {
    private BufferedImage background, foreground, output;

    public LineBoothState(BufferedImage f) {
        //if(b.getWidth() != f.getWidth() || b.getHeight() != f.getHeight()) {
//            throw new IllegalArgumentException("Image dimensions must be the same.");
  //      }

        setForeground(f);
    }

    public void setForeground(BufferedImage f) {
        this.foreground = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.background = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.output = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for(int x = 0; x < f.getWidth(); x++) {
            for(int y = 0; y < f.getHeight(); y++) {
                int rgb = f.getRGB(x, y);

                this.output.setRGB(x, y, rgb);
                this.foreground.setRGB(x, y, rgb);
                this.background.setRGB(x, y, rgb);
            }
        }
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