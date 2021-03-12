package soldasim.MTG_ER_Table.Controller;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Captures parts of the screen or windows of applications.
 */
public class ScreenCapture {

    private static Robot robot;
    private static Rectangle screen;

    public static void init() throws AWTException {
        robot = new Robot();
        screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    }

    public static BufferedImage getScreen() {
        return robot.createScreenCapture(screen);
    }

}
