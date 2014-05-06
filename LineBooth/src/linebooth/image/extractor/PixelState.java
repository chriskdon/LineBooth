package linebooth.image.extractor;

public class PixelState {
    public static enum DIRECTIONS {N, E, S, W}

    private int x, y;
    private int currentDirection;

    private boolean isOpaque;


    public PixelState(int x, int y, boolean isOpaque) {
        this.x = x;
        this.y = y;
        this.isOpaque = isOpaque;

        this.currentDirection = 0;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasAvailableDirections() {
        return currentDirection <= 3;
    }

    public int getCurrentDirection() {
        return currentDirection;
    }

    public void incrementDirection() {
        currentDirection++;
    }

    public boolean isOpaque() {
        return isOpaque;
    }

    public String getPixelPositionString() {
        return Integer.toString(x) + Integer.toString(y);
    }
}