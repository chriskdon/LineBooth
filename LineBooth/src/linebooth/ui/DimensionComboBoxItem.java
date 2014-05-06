package linebooth.ui;

import java.awt.*;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-05.
 */
public class DimensionComboBoxItem {
    private String text;
    private Dimension dimension;

    public DimensionComboBoxItem(String text, Dimension dimension) {
        this.text = text;
        this.dimension = dimension;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public static DimensionComboBoxItem[] create(Dimension[] dimens) {
        DimensionComboBoxItem[] items = new DimensionComboBoxItem[dimens.length];

        for(int i = 0; i < dimens.length; i++) {
            Dimension d = dimens[i];
            items[i] = new DimensionComboBoxItem((int)d.getWidth() + "x" + (int)d.getHeight(), d);
        }

        return items;
    }

    @Override
    public String toString() {
        return text;
    }
}
