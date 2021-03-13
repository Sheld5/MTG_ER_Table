package soldasim.MTG_ER_Table.Controller;

import com.sun.jna.Native;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import soldasim.MTG_ER_Table.View.View;

import java.util.List;

/**
 * Gets information about other running applications and captures their windows.
 */
public class ScreenCapture {

    private static final int MAX_TITLE_LENGTH = 512;
    private static final int FW_TITLE_REFRESH_RATE = 60; // times per second

    private static ForegroundWindowUpdater fwUpdater;

    /**
     * Return a list of all active windows.
     * @return a List of DesktopWindow instances describing each active window
     */
    public static List<DesktopWindow> getWindowList() {
        return WindowUtils.getAllWindows(true);
    }

    /**
     * Return the title of the window in the foreground.
     * @return a String containing the title
     */
    public static String getForegroundWindowTitle() {
        char[] title = new char[MAX_TITLE_LENGTH];
        User32 user = User32.INSTANCE;
        WinDef.HWND foregroundWindow = user.GetForegroundWindow();
        user.GetWindowText(foregroundWindow, title, MAX_TITLE_LENGTH);
        return Native.toString(title);
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
        fwUpdater = new ForegroundWindowUpdater(view);
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
     * Returns whether there is a thread running which is updating the foreground window title in the view.
     * @return true if there is a such thread running, false otherwise
     */
    static boolean isUpdatingFWTitle() {
        return fwUpdater != null;
    }

    /**
     * Runnable class which continuously gives the view updates on the foreground window title.
     */
    private static class ForegroundWindowUpdater implements Runnable {

        private final View view;
        private boolean run = true;
        private String windowTitle = "";

        ForegroundWindowUpdater(View view) {this.view = view;}

        @Override
        public void run() {
            long startTime, waitTime;

            while (run) {
                startTime = System.currentTimeMillis();

                String newWindowTitle = getForegroundWindowTitle();
                if (!newWindowTitle.equals(windowTitle)) {
                    windowTitle = newWindowTitle;
                    view.giveForegroundWindowTitle(windowTitle);
                }

                waitTime = (long)(1000 / FW_TITLE_REFRESH_RATE) - (System.currentTimeMillis() - startTime);
                if (waitTime > 0) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
    }

}
