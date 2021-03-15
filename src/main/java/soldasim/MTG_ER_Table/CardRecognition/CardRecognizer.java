package soldasim.MTG_ER_Table.CardRecognition;

import forohfor.scryfall.api.Card;
import soldasim.MTG_ER_Table.View.ViewUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Handles matching card images cut out from screenshots to cards from the deck list.
 */
public class CardRecognizer {

    private ArrayList<Card> cardList;
    private ArrayList<BufferedImage> cardImages;

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
     * Find the best match for the given card image among cards from the card list.
     * @param cardPhoto a photo of a card; should be a card from the card list
     * @return an instance of Card of the card best matching the given image
     * @see forohfor.scryfall.api.Card
     */
    public Card recognizeCard(BufferedImage cardPhoto) {
        int listSize = cardList.size();
        Card bestCard = null;
        double bestMatch = -1;
        for (int i = 0; i < listSize; i++) {
            double match = matchCard(cardPhoto, cardImages.get(i));
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
    private double matchCard(BufferedImage cardPhoto, BufferedImage cardImage) {
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
