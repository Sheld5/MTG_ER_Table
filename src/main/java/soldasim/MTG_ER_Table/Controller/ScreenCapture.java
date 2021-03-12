package soldasim.MTG_ER_Table.Controller;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Captures parts of the screen or windows of applications.
 */
public class ScreenCapture {

    private static Robot robot;
    private static Rectangle screen;
    private static Dimension screenSize;

    /**
     * Initialize. Is to be called before calling other functions.
     * @throws AWTException
     */
    public static void init() throws AWTException {
        robot = new Robot();
        screenSize = new Dimension(3840, 2160); //TODO
        screen = new Rectangle(screenSize);
    }

    /**
     * Return an image of the whole screen.
     * @return the screen captured in a BufferedImage
     */
    public static BufferedImage getScreen() {
        return robot.createScreenCapture(screen);
    }

}
