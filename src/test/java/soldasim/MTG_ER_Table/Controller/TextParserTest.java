package soldasim.MTG_ER_Table.Controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class TextParserTest {

    @Test
    public void oneWordCardName() {
        String input = "aSSdf";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("aSSdf");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void multipleWordCardName() {
        String input = "asdf gaga heeee";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("asdf gaga heeee");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void cardNameWithComma() {
        String input = "Asdf, ss";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("Asdf, ss");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void cardNameWithApostrophe() {
        String input = "asdf'ss";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("asdf'ss");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void cardNameWithExclamationMark() {
        String input = "!Co!ol!";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("!Co!ol!");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void cardWithSimpleQuantity() {
        String input = "2 mountain";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("mountain");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void cardWithMultipleDigitQuantity() {
        String input = "256 mountain";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("mountain");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void cardWithQuantityWithX() {
        String input = "8x mountain";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("mountain");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void extraWhiteSpace() {
        String input = " \taAa    b'b'b\n";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("aAa b'b'b");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void emptyLine() {
        String input = "aaa\n\nbbb\n";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("aaa");
        expected.add("bbb");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void multipleCardNames() {
        String input = "Asdf!\n" +
                "gigGlt pFOf\n\n" +
                "srou'sv aasda\n";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("Asdf!");
        expected.add("gigGlt pFOf");
        expected.add("srou'sv aasda");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void combinedTest() {
        String input = "\t \t\tAsdf!\n" +
                " 88  gigGlt, pFOf\n\n" +
                " 12x \tsrou'sv aasda\n\t\t\n";

        ArrayList<String> expected = new ArrayList<>();
        expected.add("Asdf!");
        expected.add("gigGlt, pFOf");
        expected.add("srou'sv aasda");

        ArrayList<String> actual = TextParser.parseDeckList(input);

        Assertions.assertEquals(expected, actual);
    }

}
