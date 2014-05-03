package linebooth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-30.
 */
public class DetectCloseFrame extends JFrame {
    private ArrayList<ICanDetectClose> canDetectCloseComponents = new ArrayList<ICanDetectClose>();

    public DetectCloseFrame(String name) {
        super(name);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                for(ICanDetectClose c : canDetectCloseComponents) {
                    c.onClose();
                }
            }
        });
    }

    @Override
    public Component add(Component comp) {
        if(comp instanceof ICanDetectClose) {
            canDetectCloseComponents.add((ICanDetectClose)comp);
        }

        return super.add(comp);
    }
}
