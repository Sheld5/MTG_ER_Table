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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gets information about other running applications and captures their windows.
 */
public class ScreenCapture {

    private static final int MAX_TITLE_LENGTH = 512;
    private static final int FW_UPDATER_REFRESH_RATE = 20; // times per second
    private static final int WINDOW_STREAMER_REFRESH_RATE_FOR_VIEW = 58; // times per second
    private static final int WINDOW_STREAMER_REFRESH_RATE_FOR_CARD_RECOGNITION = 2; // times per second

    private static Robot robot;
    private static String fwTitle = "";
    private static WinDef.HWND fwHandle;
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
     * Start SelectingWindowUpdater.
     * @param view a reference to the view which is to be continuously updated
     */
    static void startUpdatingWindowTitle(View view) {
        SelectedWindowUpdater.run(view);
    }

    /**
     * Stop the SelectedWindowUpdater.
     */
    static void stopUpdatingWindowTitle() {
        SelectedWindowUpdater.stop();
    }

    /**
     * Returns whether there is a SelectedWindowUpdater running.
     * @return true if there is such thread running, false otherwise
     */
    static boolean isUpdatingWindowTitle() {
        return SelectedWindowUpdater.running;
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
        WindowCapturer.run(view);
    }

    /**
     * Stop the WindowCapturer.
     */
    static void stopCapturingWindow() {
        WindowCapturer.stop();
    }

    /**
     * Set whether the WindowCapturer should send captured images to the view or to the controller.
     * @param b if true the WindowCapturer will start sending captured images to the view,
     *          otherwise it will start sending them to the controller
     */
    static void sendCapturesToView(boolean b) {
        WindowCapturer.sendToView(b);
    }

    /**
     * Returns whether the WindowCapturer is running.
     * @return true if the WindowCapturer is running, false otherwise
     */
    static boolean isCapturingWindow() {
        return WindowCapturer.running;
    }

    /**
     * Returns whether the WindowCapturer is sending to the view.
     * @return true if the WindowCapturer is sending to the view, false otherwise
     */
    static boolean isSendingToView() {
        return WindowCapturer.sendCapturesToView;
    }

    /**
     * Continuously updates the window stored in fwHandle and its title stored in fwTitle.
     * Also continuously updates the selected window title in the view accordingly.
     */
    private static class SelectedWindowUpdater {

        private static View view;
        private static boolean running;
        private static Timer timer;
        private static final int delay = 1000 / FW_UPDATER_REFRESH_RATE;
        private static ArrayList<String> thisAppWindowTitles;

        private static final ArrayList<String> ignoredWindowTitles = new ArrayList<String>() {{
            add("");
            add("Task Switching");
        }};

        private static class WindowUpdateTask extends TimerTask {
            @Override
            public void run() {
                WinDef.HWND newWindow = getForegroundWindow();
                String newWindowTitle = getWindowTitle(newWindow);
                if (windowIsAcceptable(newWindowTitle)) {
                    fwHandle = newWindow;
                    fwTitle = newWindowTitle;
                    view.giveSelectedWindowTitle(fwTitle);
                }
            }
        }

        private static void run(View view) {
            SelectedWindowUpdater.view = view;
            thisAppWindowTitles = View.getWindowTitles();
            if (!running) {
                running = true;
                timer = new Timer();
                timer.schedule(new WindowUpdateTask(), 0, delay);
            }
        }

        private static void stop() {
            if (running) {
                running = false;
                timer.cancel();
            }
        }

        private static boolean windowIsAcceptable(String newWindowTitle) {
            if (newWindowTitle.equals(fwTitle)) return false;
            if (thisAppWindowTitles.contains(newWindowTitle)) return false;
            if (ignoredWindowTitles.contains(newWindowTitle)) return false;
            return true;
        }
    }

    /**
     * Continuously takes screenshot of the window currently stored in fwHandle
     * and either gives them to the view or to the controller depending on the state of the application.
     * The window stored in fwHandle is determined by the SelectedWindowUpdater.
     */
    private static class WindowCapturer {

        private static View view;
        private static boolean running = false;
        private static boolean sendCapturesToView;
        private static Timer timer;
        private static int delay;

        static {sendCapturesToView(false);}

        private static class CaptureTask extends java.util.TimerTask {
            @Override
            public void run() {
                BufferedImage capture = captureWindow(fwHandle);
                if (sendCapturesToView) {
                    view.displayWindowCapture(capture);
                } else {
                    if (cardRecognizer != null && capture != null) {
                        cardRecognizer.giveImage(capture);
                    }
                }
            }
        }

        private static void run(View view) {
            WindowCapturer.view = view;
            if (!running) {
                running = true;
                timer = new Timer();
                timer.schedule(new CaptureTask(), 0, delay);
            }
        }

        private static void stop() {
            if (running) {
                running = false;
                timer.cancel();
            }
        }

        private static void sendToView(boolean b) {
            if (running) timer.cancel();
            sendCapturesToView = b;
            if (b) {
                delay = 1000 / WINDOW_STREAMER_REFRESH_RATE_FOR_VIEW;
            } else {
                delay = 1000 / WINDOW_STREAMER_REFRESH_RATE_FOR_CARD_RECOGNITION;
            }
            if (running) {
                timer = new Timer();
                timer.schedule(new CaptureTask(), 0, delay);
            }
        }
    }

}
