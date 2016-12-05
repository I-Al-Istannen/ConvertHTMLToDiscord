package me.ialistannen.htmltodiscord.util;

import org.jsoup.parser.Parser;

/**
 * A class dealing with Strings
 */
public class StringUtils {

    /**
     * Repeats a given String for the given amount
     *
     * @param string The String to repeat
     * @param amount The amount to repeat it for
     *
     * @return The repeated String consisting of '{@code amount times string}'
     */
    public static String repeat(String string, int amount) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            builder.append(string);
        }

        return builder.toString();
    }

    /**
     * Pads a String to a given length
     *
     * @param string The string to pad
     * @param paddingChar The padding character
     * @param length The desired length of the string
     *
     * @return The padded String, or the original string if it was {@code >= length}
     */
    public static String padToLength(String string, char paddingChar, int length) {
        String result = string.trim();

        result = Parser.unescapeEntities(result, false);

        if (result.length() >= length) {
            return result;
        }
        int difference = length - result.length();

        result = repeat(Character.toString(paddingChar), difference / 2) + result + repeat(Character.toString(paddingChar), difference / 2);
        if (difference % 2 != 0) {
            result += " ";
        }

        return result;
    }

}
