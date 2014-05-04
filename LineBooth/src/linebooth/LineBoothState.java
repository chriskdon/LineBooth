package linebooth;

import java.awt.image.BufferedImage;

public class LineBoothState {
    private BufferedImage output;

    public LineBoothState(BufferedImage f) {
        setForeground(f);
    }

    public void setForeground(BufferedImage f) {
        this.output = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for(int x = 0; x < f.getWidth(); x++) {
            for(int y = 0; y < f.getHeight(); y++) {
                int rgb = f.getRGB(x, y);

                this.output.setRGB(x, y, rgb);
            }
        }
    }

    public BufferedImage getOutput() {
        return output;
    }

    public void setOutput(BufferedImage img) {
        this.output = img;
    }

    public int getWidth() {
        return output.getWidth();
    }

    public int getHeight() {
        return output.getHeight();
    }
}