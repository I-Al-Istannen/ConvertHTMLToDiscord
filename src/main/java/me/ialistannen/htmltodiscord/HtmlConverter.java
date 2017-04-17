package me.ialistannen.htmltodiscord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

/**
 * Converts HTML to markdown
 */
public class HtmlConverter {

  private String htmlCode;
  private MapperCollection mappers;
  private ConverterStorage converterStorage;
  private ContextMetadata metadata;
  private boolean silentlyIgnoreUnknownTags;

  /**
   * Creates a new HTML to Markdown converter
   *
   * @param htmlCode The HTML code to parse
   * @param mappers The {@link Mapper}s to use for converting HTML tags to markdown
   */
  public HtmlConverter(String htmlCode, MapperCollection mappers) {
    this.htmlCode = "<root>" + htmlCode + "</root>";
    this.mappers = mappers;

    converterStorage = new ConverterStorage();
    metadata = new ContextMetadata();
  }

  /**
   * Parses the HTML
   *
   * @param baseUrl The base url of the website. Used to resolve Links
   * @return The parsed String
   */
  public String parse(String baseUrl) {
    Document document = Jsoup.parse(htmlCode, baseUrl);

    Element html = document.child(0);

    List<WrappedElement> flatten = flatten(html.child(1));
    Collections.reverse(flatten);

    // convert it from the bottom up...
    flatten.forEach(WrappedElement::getReplacedContent);

    WrappedElement last = flatten.get(flatten.size() - 1);

    return Parser.unescapeEntities(converterStorage.getReplacement(last.getWrapped()), true);
  }

  /**
   * Breath First Search (in order traversal) to flatten the tree
   *
   * @param input The tree root
   * @return The flattened tree
   */
  private List<WrappedElement> flatten(Element input) {
    Stack<Element> inputQueue = new Stack<>();
    Queue<WrappedElement> outputQueue = new LinkedList<>();

    inputQueue.add(input);

    while (!inputQueue.isEmpty()) {
      Element element = inputQueue.pop();

      outputQueue.add(
          new WrappedElement(
              element, converterStorage, mappers, metadata, silentlyIgnoreUnknownTags
          )
      );

      for (int i = 0; i < element.children().size(); i++) {
        inputQueue.push(element.child(i));
      }
    }

    outputQueue.poll();

    return new ArrayList<>(outputQueue);
  }

  /**
   * Sets whether unknown tags will be silently ignored.
   *
   * @param ignore Whether to silently ignore unknown tags
   * @return This {@link HtmlConverter}
   */
  public HtmlConverter setSilentlyIgnoreUnknownTags(boolean ignore) {
    silentlyIgnoreUnknownTags = ignore;
    return this;
  }
}
