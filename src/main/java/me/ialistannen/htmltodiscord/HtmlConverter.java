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

    private String           htmlCode;
    private MapperCollection mappers;
    private ConverterStorage converterStorage;
    private ContextMetadata  metadata;

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
     *
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

        String result = Parser.unescapeEntities(converterStorage.getReplacement(last.getWrapped()), true);
        //        StringSelection selection = new StringSelection(result);
        //        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //        clipboard.setContents(selection, selection);
        return result;
    }

    /**
     * Breath First Search (in order traversal) to flatten the tree
     *
     * @param input The tree root
     *
     * @return The flattened tree
     */
    private List<WrappedElement> flatten(Element input) {
        Stack<Element> inputQueue = new Stack<>();
        Queue<WrappedElement> outputQueue = new LinkedList<>();

        inputQueue.add(input);

        while (!inputQueue.isEmpty()) {
            Element element = inputQueue.pop();

            outputQueue.add(new WrappedElement(element, converterStorage, mappers, metadata));

            for (int i = 0; i < element.children().size(); i++) {
                inputQueue.push(element.child(i));
            }
        }

        outputQueue.poll();

        return new ArrayList<>(outputQueue);
    }

    /**
     * Some test code
     *
     * @param args The VM args
     */
    public static void main(String[] args) {
        String code = "<div class=\"block\">Class <code>Object</code> is the root of the class hierarchy.\n"
                  + " Every class has <code>Object</code> as a superclass. All objects,\n"
                  + " including arrays, implement the methods of this class.</div>";
        //        String code = "<code><b><i>HEY</i>BOLD</b></code> <input type=\"checkbox\" name=\"Kenntnisse_in\" value=\"HTML\" checked=\"checked\">";
        MapperCollection collection = new MapperCollection();
        for (StandardMappers standardMappers : StandardMappers.values()) {
            collection.addMapper(standardMappers);
        }

        HtmlConverter converter = new HtmlConverter(code, collection);
        String result = converter.parse("https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html");
        System.out.println("Result is: " + result);
    }
}
