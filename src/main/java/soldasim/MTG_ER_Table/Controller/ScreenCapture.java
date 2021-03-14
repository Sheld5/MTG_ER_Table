package soldasim.MTG_ER_Table.Controller;

import com.sun.jna.Native;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import soldasim.MTG_ER_Table.View.View;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Gets information about other running applications and captures their windows.
 */
public class ScreenCapture {

    private static final int MAX_TITLE_LENGTH = 512;
    private static final int FW_TITLE_REFRESH_RATE = 60; // times per second

    private static View view;
    private static ForegroundWindowUpdater fwUpdater;
    private static String fwTitle = "";
    private static WinDef.HWND fwHandle;
    private static Robot robot;

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
     * Start a new thread that continuously updates the foreground window title in the view.
     * Only the thread created from the last call of this function will be running.
     * @param view a reference to the view which is to be continuously updated
     */
    static void startUpdatingForegroundWindow(View view) {
        if (fwUpdater != null) {
            fwUpdater.run = false;
        }
        ScreenCapture.view = view;
        fwUpdater = new ForegroundWindowUpdater();
        Thread updaterThread = new Thread(fwUpdater);
        updaterThread.start();
    }

    /**
     * Stop updating the foreground window title in the view.
     */
    static void stopUpdatingForegroundWindow() {
        if (fwUpdater == null) return;
        fwUpdater.run = false;
        fwUpdater = null;
    }

    /**
     * Returns whether there is a thread running which is updating the foreground window title in the given view.
     * @param view the instance of View that is to be checked whether it is being updated
     * @return true if there is such thread running, false otherwise
     */
    static boolean isUpdatingFWTitle(View view) {
        return fwUpdater != null && ScreenCapture.view == view;
    }

    /**
     * Runnable class which continuously gives the view updates on the foreground window title.
     */
    private static class ForegroundWindowUpdater implements Runnable {

        private boolean run = true;

        @Override
        public void run() {
            String thisWindowTitle = View.getWindowTitle();

            while (run) {
                long startTime = System.currentTimeMillis();

                WinDef.HWND newWindow = getForegroundWindow();
                String newWindowTitle = getWindowTitle(newWindow);
                if (!(newWindowTitle.equals(fwTitle) || newWindowTitle.equals(thisWindowTitle))) {
                    fwHandle = newWindow;
                    fwTitle = newWindowTitle;
                    view.giveForegroundWindowTitle(fwTitle);
                }

                long waitTime = (long)(1000 / FW_TITLE_REFRESH_RATE) - (System.currentTimeMillis() - startTime);
                if (waitTime > 0) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ignored) {}
                }
            }
        }

    }

    /**
     * Return the title of the last application window that has been in the foreground
     * while the ForegroundWindowUpdater was running excluding the window of this application.
     * @return String containing title of the last foreground application window
     */
    public static String getLastFWTitle() {
        return fwTitle;
    }

    /**
     * Return the WinDef.HWND handle of the last application window that has been in the foreground
     * while the ForegroundWindowUpdater was running excluding the window of this application.
     * @return WinDef.HWND of the last foreground application window
     */
    public static WinDef.HWND getLastFWHandle() {
        return fwHandle;
    }

}
