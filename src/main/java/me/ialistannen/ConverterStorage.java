package me.ialistannen;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Element;

/**
 * Saves the converted Strings
 */
public class ConverterStorage {
    private Map<Element, String> replacementMap = new HashMap<>();

    /**
     * @param element The {@link Element} the replacement is for
     * @param replacement The replacement for it
     */
    void setReplacement(Element element, String replacement) {
        replacementMap.put(element, replacement);
    }

    /**
     * @param element The {@link Element} to get the replacement for
     *
     * @return The replacement for the element or an empty String if none
     */
    String getReplacement(Element element) {
        return replacementMap.get(element) == null ? "" : replacementMap.get(element);
    }
}
