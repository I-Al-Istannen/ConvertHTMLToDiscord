package me.ialistannen.htmltodiscord;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Element;

/**
 * Some metadata
 */
public class ContextMetadata {
    private Map<Element, Map<String, Object>> metadata = new HashMap<>();

    /**
     * Returns the MetaData for a given Key
     *
     * @param key The key for the metadata
     * @param element The {@link Element} to obtain them for
     * @param <T> The type of them. Used to spare you from casting it, but it means it may throw in here!
     *
     * @return The metadata or null if none
     */
    public <T> T getMetadata(String key, Element element) {
        if (!metadata.containsKey(element)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T result = (T) metadata.get(element).get(key);
        return result;
    }

    /**
     * Saves the metadata value
     *
     * @param key The key to save it as
     * @param element The {@link Element} to obtain them for
     * @param value The value to save
     */
    public void setMetadata(String key, Element element, Object value) {
        if (metadata.containsKey(element)) {
            metadata.get(element).put(key, value);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        metadata.put(element, map);
    }

    /**
     * Checks if there is metadata for a given key saved
     *
     * @param key The key to check
     * @param element The {@link Element} whose metadata to check
     *
     * @return True if it has a value for the given key
     */
    public boolean hasMetadata(String key, Element element) {
        return metadata.containsKey(element) && metadata.get(element).containsKey(key);
    }
}
