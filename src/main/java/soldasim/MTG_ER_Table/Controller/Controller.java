package soldasim.MTG_ER_Table.Controller;

import soldasim.MTG_ER_Table.CardRecognition.CardRecognizer;
import soldasim.MTG_ER_Table.View.View;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

/**
 * Controller according to the MVC application model.
 * Handles application flow and logic.
 */
public class Controller {

    private View view;
    private CardRecognizer cardRecognizer;

    /**
     * This structure is used for storing data about work requested to be done by Controller by other application modules.
     * @see WorkData
     */
    public WorkData work;

    /**
     * Initialize.
     */
    public Controller() {
        work = new WorkData();
    }

    /**
     * Start the program.
     */
    public void start() {
        waitForView();
        WebcamController.init();
        WebcamController.startStreamingWebcam(view);
        ScreenCapture.startCapturingWindow(view);
        workLoop();
        exit();
    }

    /**
     * Wait until view gives reference to itself.
     * @see View
     */
    private void waitForView() {
        synchronized (this) {
            while (view == null) {
                try {
                    this.wait();
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Give the controller a reference to the view and notify it.
     * @param view the application view instance
     * @see View
     */
    public void setView(View view) {
        synchronized (this) {
            this.view = view;
            this.notify();
        }
    }

    /**
     * Work cycle of the controller:
     * - wait for work
     * - get notified that there is work ready
     * - do all work
     * - repeat
     */
    private void workLoop() {
        while (true) {
            WorkData tmpWork;
            Lock workLock = work.lock;

            workLock.lock();
            try {
                while (!work.ready) {
                    try {
                        work.cond.await();
                    } catch (InterruptedException ignored) {}
                }
                tmpWork = work;
                work = new WorkData();
            } finally {
                workLock.unlock();
            }

            if (tmpWork.viewTerminated) break;
            doWork(tmpWork);
        }
    }

    /**
     * Do all work prepared in tmpWork.
     */
    private void doWork(WorkData work) {
        doWorkDeckList(work);
        doWorkUpdateFW(work);
    }

    /**
     * Check if there is a deck list ready, download cards if so.
     * @param work an instance of Controller.WorkData
     */
    private void doWorkDeckList(WorkData work) {
        if (work.deckList.equals("")) return;
        ArrayList<String> cardList = TextParser.parseDeckList(work.deckList);
        cardRecognizer = new CardRecognizer(cardList);
    }

    /**
     * Check if the view has requested a change in updating the foreground window title, perform the change if so.
     * @param work an instance of Controller.WorkData
     */
    private void doWorkUpdateFW(WorkData work) {
        if (work.windowSelecting == Request.WindowSelecting.Selecting.NOTHING) return;
        if (work.windowSelecting == Request.WindowSelecting.Selecting.STOP) {
            ScreenCapture.stopUpdatingFW();
            return;
        }
        if (!ScreenCapture.isUpdatingFWTitle()) {
            ScreenCapture.startUpdatingFW(view);
        }
    }

    /**
     * Actions performed before program ends.
     */
    private void exit() {
        WebcamController.stopStreamingWebcam();
        ScreenCapture.stopUpdatingFW();
        ScreenCapture.stopCapturingWindow();
    }

}
