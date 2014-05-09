package linebooth.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-09.
 */
public class JFloatSlider extends JSlider {
    private FloatActionEvent eventHandler;
    private float step;

    public JFloatSlider(float start, float end, float step, float init, FloatActionEvent eventHandler) {
        super((int) (start/step), (int) (end/step));

        this.step = step;
        this.eventHandler = eventHandler;

        setValue(init);

        addChangeListener(new SliderListener());
    }

    public void setValue(float i) {
        super.setValue((int)(i/step));
    }

    private class SliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            if (eventHandler != null) {
                eventHandler.valueChanged((float)JFloatSlider.this.getValue() * step);
            }
        }
    }

    public interface FloatActionEvent {
        public void valueChanged(float value);
    }
}
