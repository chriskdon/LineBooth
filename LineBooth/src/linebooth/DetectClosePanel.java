package linebooth;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-30.
 */
public class DetectClosePanel extends JPanel implements ICanDetectClose {
    private ArrayList<ICanDetectClose> canDetectCloseContainers = new ArrayList<ICanDetectClose>();

    @Override
    public Component add(Component comp) {
        if(comp instanceof ICanDetectClose) {
            canDetectCloseContainers.add((ICanDetectClose)comp);
        }

        return super.add(comp);
    }

    @Override
    public void onClose() {
        for(ICanDetectClose c : canDetectCloseContainers) {
            c.onClose();
        }
    }
}
