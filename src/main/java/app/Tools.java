package app;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * Tools class with static methods
 * @author Tomasz Augustyn
 */
public class Tools {

    private static final double DROP_SHADOW_OFFSET_X = 0f;
    private static final double DROP_SHADOW_OFFSET_Y = 0f;
    private static final double DROP_SHADOW_RADIUS = 16.0;

    public static void simulateClick(Control control) {
        simulateClick(control, 0, 0);
    }

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

    public static void vanishGlowEffect(Node node)
    {
        DropShadow borderGlow = new DropShadow();
        //borderGlow.setColor(Color.valueOf("#FF4855"));
        borderGlow.setColor(Color.RED);
        borderGlow.setOffsetX(DROP_SHADOW_OFFSET_X);
        borderGlow.setOffsetY(DROP_SHADOW_OFFSET_Y);
        borderGlow.setRadius(DROP_SHADOW_RADIUS);

        DropShadow vanishingShadow = (DropShadow)borderGlow.impl_copy();
        node.setEffect(vanishingShadow);
        final Timeline timeline = new Timeline();
        //timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        final KeyValue kv = new KeyValue(vanishingShadow.radiusProperty(), 0.0);
        final KeyFrame kf = new KeyFrame(Duration.millis(1000), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }
}
