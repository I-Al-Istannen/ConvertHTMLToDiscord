package me.ialistannen;

/**
 * A Mapper
 */
public interface Mapper {

    /**
     * Converts the input
     *
     * @param input The input to convert
     *
     * @return The converted input
     */
    String convert(String input);

    /**
     * Checks if this element matches the filter
     *
     * @param element The element to filter
     *
     * @return True if this element matches this mapper
     */
    default boolean matches(WrappedElement element) {
        return matches(element.getWrapped().tagName());
    }

    /**
     * Checks if this element matches the filter
     *
     * @param htmlTag The HTML tag of the element
     *
     * @return True if this element matches this mapper
     */
    boolean matches(String htmlTag);

    /**
     * Converts the input
     *
     * @param input The input to convert
     * @param context The context for conversion
     *
     * @return The converted String
     */
    default String convert(String input, WrappedElement context) {
        return convert(input);
    }
}
