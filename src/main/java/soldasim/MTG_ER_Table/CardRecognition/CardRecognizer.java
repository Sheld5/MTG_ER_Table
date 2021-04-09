package soldasim.MTG_ER_Table.CardRecognition;

import forohfor.scryfall.api.Card;
import soldasim.MTG_ER_Table.View.ViewUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handles matching card images cut out from screenshots to cards from the deck list.
 */
public class CardRecognizer implements Runnable {

    private ArrayList<Card> cardList;
    private ArrayList<BufferedImage> cardImages;
    private boolean run = true;

    private final Lock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    private BufferedImage cardImage;

    /**
     * Use the CardDownloader class to download card data and get card images needed for card matching.
     * @param cards cards that the CardRecognizer will try to match card images to
     * @see CardDownloader
     */
    public CardRecognizer(ArrayList<String> cards) {
        cardList = CardDownloader.downloadCards(cards);
        cardImages = CardDownloader.getCardImages(cardList);
    }

    /**
     * Give the CardRecognizer an image of a card to be recognized.
     * @param cardImage an image of a card cut from a captured image
     */
    public void giveImage(BufferedImage cardImage) {
        lock.lock();
        try {
            this.cardImage = cardImage;
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
                while (cardImage == null) {
                    try {
                        cond.await();
                    } catch (InterruptedException ignored) {}
                }
                tmp = cardImage;
                cardImage = null;
            } finally {
                lock.unlock();
            }

            recognizeCard(tmp);
        }
    }

    /**
     * Find the best match for the given card image among the cards in the deck list.
     * @param cardImage an image of a card to be recognized
     */
    private void recognizeCard(BufferedImage cardImage) {
        // TODO
    }


    /* - - - - - UNUSED CODE BELOW - - - - - */

    /**
     * Find the best match for the given card image among cards from the card list.
     * @param cardPhoto a photo of a card; should be a card from the card list
     * @return an instance of Card of the card best matching the given image
     * @see forohfor.scryfall.api.Card
     */
    public Card _recognizeCard(BufferedImage cardPhoto) {
        int listSize = cardList.size();
        Card bestCard = null;
        double bestMatch = -1;
        for (int i = 0; i < listSize; i++) {
            BufferedImage image = cardImages.get(i);
            if (image == null) continue;
            double match = _matchCard(cardPhoto, cardImages.get(i));
            if (match > bestMatch) {
                bestCard = cardList.get(i);
                bestMatch = match;
            }
        }

        return bestCard;
    }

    /**
     * Compare the given card photo to the given card image and return value indicating how good match there is.
     * @param cardPhoto an image of an unknown card
     * @param cardImage an image of a card the unknown card is to be compared to
     * @return a value between 0 and 1 indicating how good of a match given images are
     */
    private double _matchCard(BufferedImage cardPhoto, BufferedImage cardImage) {
        BufferedImage[] scaledImages = ViewUtils.makeImagesSameSize(cardPhoto, cardImage);
        int width = scaledImages[0].getWidth();
        int height = scaledImages[0].getHeight();
        int pixelCount = height * width;
        int sampleCount = pixelCount * 4;

        int[] photoPixels = new int[sampleCount];
        int[] imagePixels = new int[sampleCount];
        photoPixels = scaledImages[0].getRaster().getPixels(0,0, width, height, photoPixels);
        imagePixels = scaledImages[1].getRaster().getPixels(0, 0, width, height, imagePixels);

        int diff = 0;
        for (int p = 0; p < pixelCount; p++) {
            for (int s = 0; s < 3; s++) {
                int index = p*4+s;
                diff += Math.abs(photoPixels[index] - imagePixels[index]);
            }
        }
        int maxDiff = pixelCount * 3 * 255;

        double ret = 1 - (double)diff/maxDiff;
        return ret;
    }

}
