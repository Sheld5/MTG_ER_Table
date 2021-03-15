package soldasim.MTG_ER_Table.Controller;

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
    Request.WindowSelecting.Selecting windowSelecting = Request.WindowSelecting.Selecting.NOTHING;
    boolean webcamImage = false;

    public void giveRequest(Request.Interface req) {
        lock.lock();
        try {
            req.give(this);
            ready = true;
            cond.signal();
        } finally {
            lock.unlock();
        }
    }

}
