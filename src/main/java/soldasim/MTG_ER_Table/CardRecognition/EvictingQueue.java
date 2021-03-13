package soldasim.MTG_ER_Table.CardRecognition;

import java.awt.image.BufferedImage;
import java.util.EmptyStackException;
import java.util.concurrent.ArrayBlockingQueue;

public class EvictingQueue {

    private static final int BUFFER_CAPACITY = 10;

    private final ArrayBlockingQueue<BufferedImage> buffer = new ArrayBlockingQueue<>(BUFFER_CAPACITY);
    private int itemsInQueue;

    public EvictingQueue() {
        itemsInQueue = 0;
    }

    /**
     * add new item to queue, evict the oldest item from queue to make space if queue is full
     * @param item new item to be added to queue
     * @throws InterruptedException inherited from "ArrayBlockingQueue"'s method "take()"
     */
    public void add(BufferedImage item) throws InterruptedException {
        if (itemsInQueue < BUFFER_CAPACITY) {
            buffer.add(item);
            itemsInQueue += 1;
        } else {
            buffer.take();
            buffer.add(item);
        }
    }

    /**
     * @return oldest item from queue, null when called while buffer is empty
     * @throws InterruptedException inherited from "ArrayBlockingQueue"'s method "take()"
     */
    public BufferedImage take() throws EmptyStackException, InterruptedException {
        if (itemsInQueue >= 0) {
            BufferedImage ret = buffer.take();
            itemsInQueue -= 1;
            return ret;
        }
        return null;
    }

    /**
     * @return true if queue is empty, false otherwise
     */
    public boolean IsEmpty() {
        return itemsInQueue == 0;
    }

    /**
     * @return number of items in queue
     */
    public int size() {
        return itemsInQueue;
    }

    /**
     * clear all items from queue
     */
    public void clear() {
        buffer.clear();
        itemsInQueue = 0;
    }

}
