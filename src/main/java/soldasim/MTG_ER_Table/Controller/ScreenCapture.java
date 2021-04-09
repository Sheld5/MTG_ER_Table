package soldasim.MTG_ER_Table.Controller;

import com.sun.jna.Native;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import soldasim.MTG_ER_Table.CardRecognition.CardRecognizer;
import soldasim.MTG_ER_Table.View.View;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Gets information about other running applications and captures their windows.
 */
public class ScreenCapture {

    private static final int MAX_TITLE_LENGTH = 512;
    private static final int FW_UPDATER_REFRESH_RATE = 20; // times per second
    private static final int WINDOW_STREAMER_REFRESH_RATE_FOR_VIEW = 60; // times per second
    private static final int WINDOW_STREAMER_REFRESH_RATE_FOR_CARD_RECOGNITION = 20; // times per second

    private static SelectedWindowUpdater fwUpdater;
    private static WindowCapturer windowCapturer;
    private static Robot robot;

    private static String fwTitle = "";
    private static WinDef.HWND fwHandle;
    private static boolean sendCapturesToView = false;
    private static int windowCapturerRefreshRate = WINDOW_STREAMER_REFRESH_RATE_FOR_VIEW;
    private static CardRecognizer cardRecognizer;

    /**
     * Return a list of all active windows.
     * @return a List of DesktopWindow instances describing each active window
     */
    public static List<DesktopWindow> getWindowList() {
        return WindowUtils.getAllWindows(true);
    }

    /**
     * Return the WinDef.HWND handle of the window in the foreground.
     * @return WinDef.HWND of the window in the foreground
     */
    public static WinDef.HWND getForegroundWindow() {
        return User32.INSTANCE.GetForegroundWindow();
    }

    /**
     * Return the title of the given window.
     * @param window WinDef.HWND handle of a window
     * @return the title of the given window
     */
    public static String getWindowTitle(WinDef.HWND window) {
        char[] title = new char[MAX_TITLE_LENGTH];
        User32.INSTANCE.GetWindowText(window, title, MAX_TITLE_LENGTH);
        return Native.toString(title);
    }

    /**
     * Return a screenshot of the given window.
     * @param window WinDef.HWND handle of the window
     * @return BufferedImage containing an image of the window
     *         Can return null on error or if the given window does not have positive dimensions.
     */
    public static BufferedImage captureWindow(WinDef.HWND window) {
        if (window == null) return null;
        if (robot == null) {
            try {
                robot = new Robot();
            } catch (AWTException e) {
                return null;
            }
        }
        WinDef.RECT RECT = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(window, RECT);
        Rectangle border = RECT.toRectangle();
        if (!(border.width > 0 && border.height > 0)) return null;
        return robot.createScreenCapture(RECT.toRectangle());
    }

    /**
     * Return the title of the last application window that has been in the foreground
     * while the SelectedWindowUpdater was running excluding the window of this application.
     * @return String containing title of the last foreground application window
     */
    static String getLastWindowTitle() {
        return fwTitle;
    }

    /**
     * Return the WinDef.HWND handle of the last application window that has been in the foreground
     * while the SelectedWindowUpdater was running excluding the window of this application.
     * @return WinDef.HWND of the last foreground application window
     */
    static WinDef.HWND getLastWindowHandle() {
        return fwHandle;
    }

    /**
     * Start the SelectedWindowUpdater on a new thread.
     * Only the thread created from the last call of this function will be running.
     * @param view a reference to the view which is to be continuously updated
     */
    static void startUpdatingWindowTitle(View view) {
        if (fwUpdater != null) {
            fwUpdater.run = false;
        }
        fwUpdater = new SelectedWindowUpdater(view);
        Thread updaterThread = new Thread(fwUpdater);
        updaterThread.start();
    }

    /**
     * Stop the SelectedWindowUpdater.
     */
    static void stopUpdatingWindowTitle() {
        if (fwUpdater == null) return;
        fwUpdater.run = false;
        fwUpdater = null;
    }

    /**
     * Returns whether there is a SelectedWindowUpdater running.
     * @return true if there is such thread running, false otherwise
     */
    static boolean isUpdatingWindowTitle() {
        return fwUpdater != null;
    }

    /**
     * Give ScreenCapture reference to a CardRecognizer which the WindowCapturer should send images to.
     * @param cr an instance of CardRecognizer
     * @see CardRecognizer
     */
    static void setCardRecognizer(CardRecognizer cr) {
        cardRecognizer = cr;
    }

    /**
     * Start a new thread with WindowCapturer that continuously captures the selected window.
     * The window is selected by running the SelectedWindowUpdater.
     * Only the thread created from the last call of this function will be running.
     * @param view a reference to the view which is to be continuously updated
     */
    static void startCapturingWindow(View view) {
        if (windowCapturer != null) {
            windowCapturer.run = false;
        }
        windowCapturer = new WindowCapturer(view);
        Thread streamerThread = new Thread(windowCapturer);
        streamerThread.start();
    }

    /**
     * Stop the WindowCapturer.
     */
    static void stopCapturingWindow() {
        if (windowCapturer == null) return;
        windowCapturer.run = false;
        windowCapturer = null;
    }

    /**
     * Returns whether there is a WindowCapturer running.
     * @return true if there is such thread running, false otherwise
     */
    static boolean isCapturingWindow() {
        return windowCapturer != null;
    }

    /**
     * Set whether the WindowCapturer should send captured images to the view or to the controller.
     * @param b if true the WindowCapturer will start sending captured images to the view,
     *          otherwise it will start sending them to the controller
     */
    static void sendCapturesToView(boolean b) {
        sendCapturesToView = b;
        if (b) {
            windowCapturerRefreshRate = WINDOW_STREAMER_REFRESH_RATE_FOR_VIEW;
        } else {
            windowCapturerRefreshRate = WINDOW_STREAMER_REFRESH_RATE_FOR_CARD_RECOGNITION;
        }
    }

    /**
     * Runnable class which continuously updates the window stored in fwHandle and its title stored in fwTitle.
     * Also continuously updates the selected window title in the view accordingly.
     */
    private static class SelectedWindowUpdater implements Runnable {

        private final View view;
        private boolean run;
        private ArrayList<String> thisAppWindowTitles;

        private static final ArrayList<String> ignoredWindowTitles = new ArrayList<String>() {{
            add("");
            add("Task Switching");
        }};

        private SelectedWindowUpdater(View view) {
            this.view = view;
            run = true;
        }

        @Override
        public void run() {
            thisAppWindowTitles = View.getWindowTitles();

            while (run) {
                long startTime = System.currentTimeMillis();

                WinDef.HWND newWindow = getForegroundWindow();
                String newWindowTitle = getWindowTitle(newWindow);
                if (windowIsAcceptable(newWindowTitle)) {
                    fwHandle = newWindow;
                    fwTitle = newWindowTitle;
                    view.giveSelectedWindowTitle(fwTitle);
                }

                long waitTime = (long)(1000 / FW_UPDATER_REFRESH_RATE) - (System.currentTimeMillis() - startTime);
                if (waitTime > 0) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ignored) {}
                }
            }
        }

        private boolean windowIsAcceptable(String newWindowTitle) {
            if (newWindowTitle.equals(fwTitle)) return false;
            if (thisAppWindowTitles.contains(newWindowTitle)) return false;
            if (ignoredWindowTitles.contains(newWindowTitle)) return false;
            return true;
        }

    }

    /**
     * Runnable class which continuously takes screenshot of the window currently stored in fwHandle
     * and either gives them to the view or to the controller depending on the state of the application.
     * The window stored in fwHandle is determined by the SelectedWindowUpdater.
     */
    private static class WindowCapturer implements Runnable {

        private final View view;
        private boolean run;

        private WindowCapturer(View view) {
            this.view = view;
            run = true;
        }

        @Override
        public void run() {
            while (run) {
                long startTime = System.currentTimeMillis();

                BufferedImage capture = captureWindow(fwHandle);
                if (sendCapturesToView) {
                    view.displayWindowCapture(capture);
                } else {
                    if (capture != null) {
                        cardRecognizer.giveImage(capture);
                    }
                }

                long waitTime = (long)(1000 / windowCapturerRefreshRate) - (System.currentTimeMillis() - startTime);
                if (waitTime > 0) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ignored) {}
                }
            }
        }

    }

}
