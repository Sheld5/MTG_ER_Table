package soldasim.MTG_ER_Table.Controller;

import forohfor.scryfall.api.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CardDownloader {

    private ArrayList<Card> cards;
    private ArrayList<BufferedImage> cardImages;

    public CardDownloader() {
        cards = new ArrayList<>();
        cardImages = new ArrayList<>();
    }

    public void downloadCards(ArrayList<String> cardNames) {
        cards = MTGCardQuery.toCardList(cardNames, false);
        cardImages = new ArrayList<>();
        for (Card card : cards) {
            cardImages.add(card.getImage());
        }
    }

    public void clearCards() {
        cards = new ArrayList<>();
        cardImages = new ArrayList<>();
    }

    public ArrayList<Card> getCards() {
        return (ArrayList<Card>) cards.clone();
    }

    public ArrayList<BufferedImage> getCardImages() {
        return (ArrayList<BufferedImage>) cardImages.clone();
    }

}
