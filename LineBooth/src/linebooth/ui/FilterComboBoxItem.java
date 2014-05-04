package linebooth.ui;

import linebooth.IFilter;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public class FilterComboBoxItem {
    private IFilter filter;
    private String text;

    public FilterComboBoxItem(String text, IFilter filter) {
        this.filter = filter;
        this.text = text;
    }

    public IFilter getFilter() {
        return filter;
    }

    @Override
    public String toString() {
        return text;
    }
}
