package soldasim.MTG_ER_Table.Controller;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Structure used to store data about work that has been requested by other modules of the application.
 * Controller uses this data later to perform the requested work.
 */
public class WorkData {

    final Lock lock = new ReentrantLock();
    final Condition cond = lock.newCondition();
    boolean ready = false;

    Boolean viewTerminated = false;
    String deckList = "";
    WorkRequest.Updating windowSelecting = WorkRequest.Updating.NOTHING;
    WorkRequest.Updating windowStreaming = WorkRequest.Updating.NOTHING;

    /**
     * Called by other modules to request work to be done by the controller.
     * @param req an instance of one of the request classes in Controller.Request
     */
    public void giveRequest(WorkRequest.Interface req) {
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
