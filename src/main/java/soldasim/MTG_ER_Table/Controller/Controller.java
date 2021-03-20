package soldasim.MTG_ER_Table.Controller;

import soldasim.MTG_ER_Table.CardRecognition.CardDownloader;
import soldasim.MTG_ER_Table.View.View;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

/**
 * Controller according to the MVC application model.
 * Handles application flow and logic.
 */
public class Controller {

    private View view;

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
        ScreenCapture.stopUpdatingFW();
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
        doWorkUpdateFW(work);
    }

    /**
     * Check if there is a deck list ready, display the first card if so.
     * @param work an instance of Controller.WorkData containing all requested work
     */
    private void doWorkDeckList(WorkData work) {
        if (work.deckList.equals("")) return;
        ArrayList<String> cardList = TextParser.parseDeckList(work.deckList);
        ArrayList<BufferedImage> cardImages = CardDownloader.getCardImages(CardDownloader.downloadCards(cardList));
        if (cardImages.size() == 0) {
            view.displayCardImage(null);
        } else {
            view.displayCardImage(cardImages.get(0));
        }
    }

    /**
     * Check if the view has requested a change in updating the foreground window title, perform the change if so.
     * @param work an instance of Controller.WorkData containing all requested work
     */
    private void doWorkUpdateFW(WorkData work) {
        if (work.windowSelecting == WorkRequest.WindowSelecting.Selecting.NOTHING) return;
        if (work.windowSelecting == WorkRequest.WindowSelecting.Selecting.STOP) {
            ScreenCapture.stopUpdatingFW();
            return;
        }
        if (!ScreenCapture.isUpdatingFWTitle()) {
            ScreenCapture.startUpdatingFW(view);
        }
    }

}
