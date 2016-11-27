package me.ialistannen;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.stream.Collectors;

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

    public HtmlConverter(String htmlCode, MapperCollection mappers) {
        this.htmlCode = "<root>" + htmlCode + "</root>";
        this.mappers = mappers;

        converterStorage = new ConverterStorage();
    }

    public void parse() {
        Document document = Jsoup.parse(htmlCode, "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/inventory/InventoryDragEvent.html");

        Element html = document.child(0);

        List<WrappedElement> flatten = flatten(html.child(1));
        Collections.reverse(flatten);

        System.out.println(flatten.stream()
                  .map(wrappedElement -> wrappedElement.getWrapped().tagName() + " '" + wrappedElement.getReplacedContent() + "'")
                  .collect(Collectors.counting()));
        WrappedElement last = flatten.get(flatten.size() - 1);
        System.out.println("Last: " + last.getWrapped().tagName() + "\t> " + converterStorage.getReplacement(last.getWrapped()));

        String result = Parser.unescapeEntities(converterStorage.getReplacement(last.getWrapped()), true);
        StringSelection selection = new StringSelection(result);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private List<WrappedElement> flatten(Element input) {
        Stack<Element> inputQueue = new Stack<>();
        Queue<WrappedElement> outputQueue = new LinkedList<>();

        inputQueue.add(input);

        while (!inputQueue.isEmpty()) {
            Element element = inputQueue.pop();

            outputQueue.add(new WrappedElement(element, converterStorage, mappers));

            for (int i = 0; i < element.children().size(); i++) {
                inputQueue.push(element.child(i));
            }
        }

        outputQueue.poll();

        return new ArrayList<>(outputQueue);
    }

    public static void main(String[] args) {
        String code = "<li class=\"blockList\">\n"
                  + "<h4>valueOf</h4>\n"
                  + "<pre>public static&nbsp;<a href=\"../../../../org/bukkit/event/inventory/InventoryAction.html\" title=\"enum in org.bukkit.event.inventory\">InventoryAction</a>&nbsp;valueOf(<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/String.html?is-external=true\" title=\"class or interface in java.lang\">String</a>&nbsp;name)</pre>\n"
                  + "<div class=\"block\">Returns the enum constant of this type with the specified name.\n"
                  + "The string must match <i>exactly</i> an identifier used to declare an\n"
                  + "enum constant in this type. (Extraneous whitespace characters are\n"
                  + "not permitted.)</div>\n"
                  + "<dl><dt><span class=\"strong\">Parameters:</span></dt><dd><code>name</code> - the name of the enum constant to be returned.</dd>\n"
                  + "<dt><span class=\"strong\">Returns:</span></dt><dd>the enum constant with the specified name</dd>\n"
                  + "<dt><span class=\"strong\">Throws:</span></dt>\n"
                  + "<dd><code><a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/IllegalArgumentException.html?is-external=true\" title=\"class or interface in java.lang\">IllegalArgumentException</a></code> - if this enum type has no constant with the specified name</dd>\n"
                  + "<dd><code><a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/NullPointerException.html?is-external=true\" title=\"class or interface in java.lang\">NullPointerException</a></code> - if the argument is null</dd></dl>\n"
                  + "</li>";
        //        String code = "<code><b><i>HEY</i>BOLD</b></code> <input type=\"checkbox\" name=\"Kenntnisse_in\" value=\"HTML\" checked=\"checked\">";
        MapperCollection collection = new MapperCollection();
        for (StandardMappers standardMappers : StandardMappers.values()) {
            collection.addMapper(standardMappers);
        }

        HtmlConverter converter = new HtmlConverter(code, collection);
        converter.parse();
    }
}
