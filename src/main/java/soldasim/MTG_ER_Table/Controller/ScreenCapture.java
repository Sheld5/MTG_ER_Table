package soldasim.MTG_ER_Table.Controller;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Captures parts of the screen or windows of applications.
 */
public class ScreenCapture {

    private static Robot robot;
    private static Rectangle screen;

    /**
     * Initialize. Is to be called before calling other functions.
     * @throws AWTException
     */
    public static void init() throws AWTException {
        robot = new Robot();
        screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    }

    /**
     * Initialize. Is to be called before calling other functions.
     * Set the screen size to the given size instead of trying to get the screen size from the system.
     * @param screenSize the size of the screen in pixels
     * @throws AWTException
     */
    public static void init(Dimension screenSize) throws AWTException {
        robot = new Robot();
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
