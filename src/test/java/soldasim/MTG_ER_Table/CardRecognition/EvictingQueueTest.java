package soldasim.MTG_ER_Table.CardRecognition;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class EvictingQueueTest {

    @Test
    void constructQueueEmpty() {
        EvictingQueue queue = new EvictingQueue();
        assertEquals(0, queue.size());
    }

    @Test
    void addAndTake() {
        EvictingQueue queue = new EvictingQueue();
        BufferedImage addedItem = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        try {
            queue.add(addedItem);
            assertEquals(1, queue.size());
            BufferedImage takenItem = queue.take();
            assertEquals(addedItem, takenItem);
            assertEquals(0, queue.size());
        }
        catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    void takeEmptyQueue() {
        EvictingQueue queue = new EvictingQueue();
        try {
            //System.out.print("going to request take() from empty queue\n");
            BufferedImage noItem = queue.take();
            //System.out.print("take() from empty queue finished\n");
            assertEquals(null, noItem);
            assertEquals(0, queue.size());
        }
        catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    void addAndTakeOrder() {
        EvictingQueue queue = new EvictingQueue();
        BufferedImage item1 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        BufferedImage item2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        BufferedImage item3 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        try {
            queue.add(item1);
            queue.add(item2);
            queue.add(item3);
            BufferedImage takenItem1 = queue.take();
            BufferedImage takenItem2 = queue.take();
            BufferedImage takenItem3 = queue.take();
            assertEquals(item1, takenItem1);
            assertEquals(item2, takenItem2);
            assertEquals(item3, takenItem3);
        }
        catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    void addFullQueue () {
        EvictingQueue queue = new EvictingQueue();
        BufferedImage item0 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        BufferedImage item1 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        BufferedImage item2 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        try {
            queue.add(item0);
        }
        catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
        }
        for (int i = 1; i < EvictingQueue.BUFFER_CAPACITY; i++) {
            try {
                queue.add(item1);
            }
            catch (InterruptedException exception) {
                System.out.println(exception.getMessage());
            }
        }
        assertEquals(EvictingQueue.BUFFER_CAPACITY, queue.size());
        try {
            queue.add(item2);
        }
        catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
        }
        assertEquals(EvictingQueue.BUFFER_CAPACITY, queue.size());

        try {
            BufferedImage takenFirst = queue.take();
            assertEquals(item1, takenFirst);
        }
        catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
        }
        for (int i = 0; i < EvictingQueue.BUFFER_CAPACITY - 2; i++) {
            try {
                queue.take();
            }
            catch (InterruptedException exception) {
                System.out.println(exception.getMessage());
            }
        }
        assertEquals(1, queue.size());
        try {
            BufferedImage takenLast = queue.take();
            assertEquals(item2, takenLast);
        }
        catch (InterruptedException exception) {
            System.out.println(exception.getMessage());
        }
    }

}
