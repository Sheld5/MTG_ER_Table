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
    private boolean workReady = false;

    private String deckList = "";

    /**
     * Start the program.
     */
    public void start() {
        waitForView();
        workLoop();
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
                doWork();
                workReady = false;
            } finally {
                workLock.unlock();
            }

        }
    }

    /**
     * Do all work that is ready.
     * This function is called with acquired workLock.
     */
    private void doWork() {
        doWorkDeckList();
    }

    /**
     * Called by the view to give controller deck list from the user to be processed.
     * @param deckList String containing individual cards on separate lines
     *                 Can contain additional white-spaces and card quantities.
     */
    public void giveWorkDeckList(String deckList) {
        workLock.lock();
        try {
            this.deckList = deckList;
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
        String tmp;
        workLock.lock();
        try {
            if (deckList.equals("")) return;
            tmp = deckList;
            deckList = "";
        } finally {
            workLock.unlock();
        }
        view.giveCardList(TextParser.parseDeckList(tmp));
    }

}
