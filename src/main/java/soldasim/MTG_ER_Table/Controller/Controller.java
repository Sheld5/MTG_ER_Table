package soldasim.MTG_ER_Table.Controller;

import soldasim.MTG_ER_Table.View.View;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Controller according to the MVC application model.
 * Handles application flow and logic.
 */
public class Controller {

    private View view;

    private final Lock workLock = new ReentrantLock();
    private final Condition workCond = workLock.newCondition();
    private boolean workReady;

    private WorkData work;
    private WorkData tmpWork;

    /**
     * Initialize.
     */
    public Controller() {
        initWorkStructure();
    }

    private void initWorkStructure() {
        workReady = false;
        work = new WorkData();
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
            workLock.lock();
            try {
                while (!workReady) {
                    try {
                        workCond.await();
                    } catch (InterruptedException ignored) {}
                }
                copyAndClearWork();
                workReady = false;
            } finally {
                workLock.unlock();
            }

            if (tmpWork.viewTerminated) break;
            doWork();
        }
    }

    /**
     * Copy work to tmpWork and clear work.
     */
    private void copyAndClearWork() {
        tmpWork = work;
        work = new WorkData();
    }

    /**
     * Do all work prepared in tmpWork.
     */
    private void doWork() {
        doWorkDeckList();
    }

    /**
     * Called by the view on termination.
     * @see View
     */
    public void notifyViewTerminated() {
        workLock.lock();
        try {
            work.viewTerminated = true;
            workReady = true;
            workCond.signal();
        } finally {
            workLock.unlock();
        }
    }

    /**
     * Called by the view to give controller deck list from the user to be processed.
     * Set workReady to true and signal controller that there is work ready.
     * @param deckList String containing individual cards on separate lines
     *                 Can contain additional white-spaces and card quantities.
     * @see View
     */
    public void giveWorkDeckList(String deckList) {
        workLock.lock();
        try {
            work.deckList = deckList;
            workReady = true;
            workCond.signal();
        } finally {
            workLock.unlock();
        }
    }

    /**
     * Check if there is a deck list waiting to be parsed,
     * parse it and give to the view if so.
     */
    private void doWorkDeckList() {
        if (tmpWork.deckList.equals("")) return;
        view.giveCardList(TextParser.parseDeckList(tmpWork.deckList));
    }

    /**
     * Actions performed before program ends.
     */
    private void exit() {
        // nothing
    }

}
