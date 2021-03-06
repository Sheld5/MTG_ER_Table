package soldasim.MTG_ER_Table.CardRecognition;

import forohfor.scryfall.api.Card;
import nu.pattern.OpenCV;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import soldasim.MTG_ER_Table.View.View;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Handles card recognition from images.
 */
public class CardRecognizer implements Runnable {

    private static final int BATCH_SIZE = 100000; //pixels

    private final View view;
    private final ArrayList<Card> cardList;
    private final ArrayList<BufferedImage> cardImages;
    private boolean run = true;

    private final Lock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    private BufferedImage capturedImage;

    static {
        OpenCV.loadLocally();
    }

    /**
     * Use the CardDownloader class to download card data and get card images needed for card matching.
     * @param cards cards that the CardRecognizer will try to match card images to
     * @see CardDownloader
     */
    public CardRecognizer(View view, ArrayList<String> cards) {
        this.view = view;
        cardList = CardDownloader.downloadCards(cards);
        cardImages = CardDownloader.getCardImages(cardList);
    }

    /**
     * Give the CardRecognizer a captured image of the board to find and recognize cards from.
     * @param cardImage an image of a card cut from a captured image
     */
    public void giveImage(BufferedImage cardImage) {
        lock.lock();
        try {
            this.capturedImage = cardImage;
            cond.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stop the CardRecognizer update loop.
     */
    public void stop() {
        run = false;
    }

    @Override
    public void run() {
        while (run) {
            BufferedImage tmp;

            lock.lock();
            try {
                while (capturedImage == null) {
                    try {
                        cond.await();
                    } catch (InterruptedException ignored) {}
                }
                tmp = capturedImage;
                capturedImage = null;
            } finally {
                lock.unlock();
            }

            findAndRecognizeCards(tmp);
        }
    }

    /**
     * Recognize cards in the given image.
     * @param capturedImage a captured image of the game board; has to be BufferedImage of type TYPE_INT_RGB
     * @return a List of recognized cards
     */
    private List<Card> findAndRecognizeCards(BufferedImage capturedImage) {
        if (capturedImage.getType() != BufferedImage.TYPE_INT_RGB) return null;

        Mat gameBoard = bufferedImageToMat(capturedImage);
        List<Mat> cardImages = findCards(gameBoard);
        List<Card> cards = recognizeCards(cardImages);

        return cards;
    }

    /**
     * Get separate card images from the captured image of the game board.
     * @param capturedImage a Mat of type CV_8UC3 containing the game board
     * @return a List of Mat of type CV_8UC3 of cards on the game board
     */
    private List<Mat> findCards(Mat capturedImage) {

        // TODO

        return null;
    }

    /**
     * Find the best match for each of the given card images among the cards in the deck list.
     * @param cardImages a List of Mat of type CV_8UC3 of cards to be recognized
     * @return a List of recognized cards
     */
    private List<Card> recognizeCards(List<Mat> cardImages) {

        // TODO

        return null;
    }

    /**
     * Create a Mat from given BufferedImage.
     * @param image an instance of BufferedImage of type TYPE_INT_RGB
     * @return an instance of Mat of type CV_8UC3,
     *         returns null if the BufferedImage was of wrong type
     */
    private Mat bufferedImageToMat(BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_INT_RGB) return null;

        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = image.getRGB(0 ,0, width, height, null, 0, width);
        byte[] data = new byte[height * width * 3];

        int di = 0;
        for (int pixel : pixels) {
            data[di++] = (byte) ((pixel >> 16) & 0xFF);
            data[di++] = (byte) ((pixel >> 8) & 0xFF);
            data[di++] = (byte) (pixel & 0xFF);
        }

        Mat mat = new Mat(height, width, CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }

    /**
     * Create a BufferedImage from given Mat.
     * @param mat an instance of Mat of type CV_8UC3
     * @return an instance of BufferedImage of type TYPE_INT_RGB,
     *         returns null if the mat was of wrong type
     */
    private BufferedImage matToBufferedImage(Mat mat) {
        if (mat.type() != CvType.CV_8UC3) return null;

        int width = mat.width();
        int height = mat.height();

        byte[] data = new byte[height * width * 3];
        mat.get(0, 0, data);
        int[] pixels = new int[height * width];

        int di = 0;
        for (int pi = 0; pi < pixels.length; pi++) {
            pixels[pi] = ((data[di++] & 0xFF) << 16) | ((data[di++] & 0xFF) << 8) | (data[di++] & 0xFF);
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, width, height, pixels);
        return image;
    }

}
