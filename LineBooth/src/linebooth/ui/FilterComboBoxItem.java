package linebooth.ui;

import linebooth.image.filters.Filter;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public class FilterComboBoxItem {
    private Filter filter;
    private String text;

    public FilterComboBoxItem(String text, Filter filter) {
        this.filter = filter;
        this.text = text;
    }

    public Filter getFilter() {
        return filter;
    }

    @Override
    public String toString() {
        return text;
    }
}
