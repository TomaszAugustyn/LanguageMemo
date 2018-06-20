package app;

import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import java.awt.*;
import java.awt.event.InputEvent;

/**
 * App for creating language memos supporting learining new words (JavaFX).
 * @author Tomasz Augustyn
 */
public class Tools {

    public static void simulateClick(Control control, double offsetX , double offsetY ) {
        Point originalLocation = MouseInfo.getPointerInfo().getLocation();
        Point2D buttonLocation = control.localToScreen(control.getLayoutBounds().getMinX() + offsetX, control.getLayoutBounds().getMinY() + offsetY);
        try {
            Robot robot = new Robot();
            robot.mouseMove((int)buttonLocation.getX(), (int)buttonLocation.getY());
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            robot.mouseMove((int) originalLocation.getX(), (int)originalLocation.getY());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void simulateClick(Control control) {
        simulateClick(control, 0, 0);
    }
}
