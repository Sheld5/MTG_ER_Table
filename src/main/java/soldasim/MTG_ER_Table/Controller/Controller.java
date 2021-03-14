package soldasim.MTG_ER_Table.Controller;

import soldasim.MTG_ER_Table.View.View;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

/**
 * Controller according to the MVC application model.
 * Handles application flow and logic.
 */
public class Controller {

    private View view;
    private final CardDownloader cardDownloader;

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
        cardDownloader = new CardDownloader();
    }

    /**
     * Start the program.
     */
    public void start() {
        waitForView();
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
        ArrayList<String> cardNames = TextParser.parseDeckList(work.deckList);
        cardDownloader.downloadCards(cardNames);
        view.giveCardImages(cardDownloader.getCardImages());
    }

    /**
     * Check if the view has requested a change in updating the foreground window title, perform the change if so.
     * @param work an instance of Controller.WorkData
     */
    private void doWorkUpdateFW(WorkData work) {
        if (work.updateFW == WorkData.Update.NOTHING) return;
        if (work.updateFW == WorkData.Update.STOP) {
            ScreenCapture.stopUpdatingForegroundWindow();
            return;
        }
        if (!ScreenCapture.isUpdatingFWTitle()) {
            ScreenCapture.startUpdatingForegroundWindow(view);
        }
    }

    /**
     * Actions performed before program ends.
     */
    private void exit() {
        ScreenCapture.stopUpdatingForegroundWindow();
    }

}
