package me.ialistannen.htmltodiscord;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;

/**
 * A wrapped element
 */
public class WrappedElement {

  private ConverterStorage converterStorage;
  private MapperCollection mappers;
  private ContextMetadata metadata;
  private Element wrapped;

  private boolean silentlyIgnoreUnknownTags;

  public WrappedElement(Element element, ConverterStorage converterStorage,
      MapperCollection mappers, ContextMetadata metadata,
      boolean silentlyIgnoreUnknownTags) {
    this.wrapped = element;
    this.converterStorage = converterStorage;
    this.mappers = mappers;
    this.metadata = metadata;
    this.silentlyIgnoreUnknownTags = silentlyIgnoreUnknownTags;
  }

  public Element getWrapped() {
    return wrapped;
  }

  public ConverterStorage getConverterStorage() {
    return converterStorage;
  }

  public MapperCollection getMappers() {
    return mappers;
  }

  /**
   * @return The metadata for this element
   */
  public ContextMetadata getMetadata() {
    return metadata;
  }

  public String getReplacedContent() {
    String html = wrapped.html();
    // replace artificial new lines before tags
    html = cleanupHtmlTagLinefeeds(html);

    for (int i = 0; i < wrapped.children().size(); i++) {
      Element child = wrapped.child(i);

      Matcher matcher = Pattern.compile(
          Pattern.quote(
              cleanupHtmlTagLinefeeds(child.outerHtml())
          )
      ).matcher(html);

      if (matcher.find()) {
        String replacement = converterStorage.getReplacement(child);

        if (matcher.end() < html.length() - 1) {
          int character = html.codePointAt(matcher.end());
          if (!Character.isWhitespace(character)) {
            replacement += " ";
          }
        }

        html = matcher.replaceFirst(replacement.replace("\\)", "\\\\)"));
      }
    }

    converterStorage.setReplacement(wrapped, html);
    html = replace(html);
    converterStorage.setReplacement(wrapped, html);

    return html;
  }

  private String cleanupHtmlTagLinefeeds(String html) {
    return html.replaceAll("(\n|\r\n|\r)\\s*<", "<");
  }

  private String replace(String content) {
    Optional<Mapper> mapperOptional = mappers.getMapper(this);
    if (mapperOptional.isPresent()) {
      return mapperOptional.get().convert(content, this);
    }
    if (silentlyIgnoreUnknownTags) {
      return content;
    }
    throw new IllegalArgumentException(
        "No mapper for tag '" + wrapped.tagName() + "' found.\n" + wrapped.outerHtml());
  }
}
