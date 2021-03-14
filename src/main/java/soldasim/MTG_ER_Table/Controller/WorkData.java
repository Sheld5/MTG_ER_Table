package soldasim.MTG_ER_Table.Controller;

import soldasim.MTG_ER_Table.View.View;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Structure used to store data about work that has been requested by other modules of the application.
 * Other models of the application (running on other threads) use methods of this class to fill it with work data.
 * Controller uses this data later to perform the requested work.
 */
public class WorkData {

    final Lock lock = new ReentrantLock();
    final Condition cond = lock.newCondition();
    boolean ready = false;

    Boolean viewTerminated = false;
    String deckList = "";
    Update updateFW = Update.NOTHING;

    public enum Update {
        START,
        STOP,
        NOTHING
    }

    /**
     * Called by the view on termination.
     * @see View
     */
    public void notifyViewTerminated() {
        lock.lock();
        try {
            viewTerminated = true;
            ready = true;
            cond.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Called by the view to give controller deck list from the user to be processed.
     * @param deckList String containing individual cards on separate lines
     *                 Can contain additional white-spaces and card quantities.
     * @see View
     */
    public void giveDeckList(String deckList) {
        lock.lock();
        try {
            this.deckList = deckList;
            ready = true;
            cond.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Called by the view to request the controller to start or stop updating the foreground window title.
     * @param update value which determines whether the updating is to be started or stopped
     * @see View
     */
    public void requestUpdateFW(Update update) {
        lock.lock();
        try {
            this.updateFW = update;
            ready = true;
            cond.signal();
        } finally {
            lock.unlock();
        }
    }

}
