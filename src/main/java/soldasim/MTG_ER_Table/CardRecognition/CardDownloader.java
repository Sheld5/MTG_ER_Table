package soldasim.MTG_ER_Table.Controller;

import forohfor.scryfall.api.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Downloads card data from Scryfall using its API.
 */
public class CardDownloader {

    /**
     * Download cards from Scryfall according to the given card list.
     * @param cardNames an ArrayList containing card names
     * @return an ArrayList containing downloaded cards as instances of Card
     * @see forohfor.scryfall.api.Card
     */
    public static ArrayList<Card> downloadCards(ArrayList<String> cardNames) {
        return MTGCardQuery.toCardList(cardNames, false);
    }

    /**
     * Create a list of card images from a list of cards.
     * @param cards an ArrayList of cards as instances of Card
     * @return an ArrayList of card images as instances of BufferedImage
     * @see forohfor.scryfall.api.Card
     */
    public static ArrayList<BufferedImage> getCardImages(ArrayList<Card> cards) {
        ArrayList<BufferedImage> cardImages = new ArrayList<>();
        for (Card card : cards) {
            cardImages.add(card.getImage());
        }
        return cardImages;
    }

}
