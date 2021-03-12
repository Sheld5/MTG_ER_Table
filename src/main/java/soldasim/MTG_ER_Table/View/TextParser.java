package soldasim.MTG_ER_Table.View;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Provides text utilities.
 */
public class TextParser {

    /**
     * Parse given deck list to a list of card names.
     * @param deckList the deck list where each line contains a name of a single card
     *                 (can contain card quantities as well)
     * @return a list containing names of cards from the deck
     */
    public static ArrayList<String> parseDeckList(String deckList) {
        ArrayList<String> cardNames = new ArrayList<>();
        Pattern namePattern = Pattern.compile("^[a-zA-Z',!]+$");

        String[] lines = deckList.split("\\r?\\n");
        for (String line : lines) {
            StringBuilder cardName = new StringBuilder();
            String[] words = line.split(" ");
            boolean empty = true;
            for (String word : words) {
                if (namePattern.matcher(word).matches()) {
                    if (empty) {
                        empty = false;
                    } else {
                        cardName.append(" ");
                    }
                    cardName.append(word);
                }
            }
            cardNames.add(cardName.toString());
        }

        return cardNames;
    }

}
