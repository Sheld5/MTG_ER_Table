package soldasim.MTG_ER_Table.CardRecognition;

import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;

public class CardRecognitionController implements Runnable {
    private EvictingQueue imageBuffer;
    private boolean running;

    /**
     * Initialize.
     */
    public CardRecognitionController() {
        imageBuffer = new EvictingQueue();
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            if (imageBuffer.size() > 0) {
                // send image to ImageProcessor
            }
        }
    }

    /**
     * end update loop
     */
    public void stop() {
        running = false;
    }

    /**
     * add image to imageBuffer for processing by CardRecognition package
     * @param image new image to be processed
     * @return true for succes, false if failed to add image (due to InterruptedException)
     */
    public boolean addImage(BufferedImage image) {
        try {
            imageBuffer.add(image);
        } catch (InterruptedException exception) {
            System.out.print("InterruptedException: Failed to add an image to imageBuffer.\n");
            return false;
        }
        return true;
    }

}
