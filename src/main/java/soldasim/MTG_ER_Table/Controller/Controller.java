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
        ScreenCapture.startCapturingWindow(view);
        workLoop();
        exit();
    }

    /**
     * Actions performed before program ends.
     */
    private void exit() {
        ScreenCapture.stopUpdatingWindowTitle();
        ScreenCapture.stopCapturingWindow();
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
        doWorkWindowSelecting(work);
        doWorkWindowStreaming(work);
    }

    /**
     * Check if there is a deck list ready, initialize a new CardRecognizer with the new deck list
     * and start it on a separate thread if so.
     * @param work an instance of Controller.WorkData containing all requested work
     */
    private void doWorkDeckList(WorkData work) {
        if (work.deckList.equals("")) return;

        ScreenCapture.setCardRecognizer(null);
        if (cardRecognizer != null) cardRecognizer.stop();

        ArrayList<String> cardList = TextParser.parseDeckList(work.deckList);
        cardRecognizer = new CardRecognizer(cardList);
        Thread cardRecognizerThread = new Thread(cardRecognizer);
        cardRecognizerThread.start();
        ScreenCapture.setCardRecognizer(cardRecognizer);
    }

    /**
     * Check if the view has requested a change in updating the selected window title, perform the change if so.
     * @param work an instance of Controller.WorkData containing all requested work
     */
    private void doWorkWindowSelecting(WorkData work) {
        if (work.windowSelecting == WorkRequest.Updating.NOTHING) return;
        if (work.windowSelecting == WorkRequest.Updating.STOP) {
            ScreenCapture.stopUpdatingWindowTitle();
            return;
        }
        if (!ScreenCapture.isUpdatingWindowTitle()) {
            ScreenCapture.startUpdatingWindowTitle(view);
        }
    }

    /**
     * Check if the view has requested to start or stop streaming the selected window to it, perform the change if so.
     * @param work an instance of Controller.WorkData containing all requested work
     */
    private void doWorkWindowStreaming(WorkData work) {
        if (work.windowStreaming == WorkRequest.Updating.NOTHING) return;
        ScreenCapture.sendCapturesToView(work.windowStreaming == WorkRequest.Updating.START);
    }

}
