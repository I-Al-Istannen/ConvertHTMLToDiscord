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

        StringSelection selection = new StringSelection(converterStorage.getReplacement(last.getWrapped()));
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
        String code = "This event is called when the player drags an item in their cursor across\n"
                  + "the inventory. The ItemStack is distributed across the slots the\n"
                  + "HumanEntity dragged over. The method of distribution is described by the\n"
                  + "DragType returned by <a href=\"../../../../org/bukkit/event/inventory/InventoryDragEvent.html#getType()\"><code>getType()</code></a>.\n"
                  + "<p>\n"
                  + "Canceling this event will result in none of the changes described in\n"
                  + "<a href=\"../../../../org/bukkit/event/inventory/InventoryDragEvent.html#getNewItems()\"><code>getNewItems()</code></a> being applied to the Inventory.\n"
                  + "<p>\n"
                  + "Because InventoryDragEvent occurs within a modification of the Inventory,\n"
                  + "not all Inventory related methods are safe to use.\n"
                  + "<p>\n"
                  + "The following should never be invoked by an EventHandler for\n"
                  + "InventoryDragEvent using the HumanEntity or InventoryView associated with\n"
                  + "this event.\n"
                  + "<ul>\n"
                  + "<li><a href=\"../../../../org/bukkit/entity/HumanEntity.html#closeInventory()\"><code>HumanEntity.closeInventory()</code></a>\n"
                  + "<li><a href=\"../../../../org/bukkit/entity/HumanEntity.html#openInventory(org.bukkit.inventory.Inventory)\"><code>HumanEntity.openInventory(Inventory)</code></a>\n"
                  + "<li><a href=\"../../../../org/bukkit/entity/HumanEntity.html#openWorkbench(org.bukkit.Location,%20boolean)\"><code>HumanEntity.openWorkbench(Location, boolean)</code></a>\n"
                  + "<li><a href=\"../../../../org/bukkit/entity/HumanEntity.html#openEnchanting(org.bukkit.Location,%20boolean)\"><code>HumanEntity.openEnchanting(Location, boolean)</code></a>\n"
                  + "<li><a href=\"../../../../org/bukkit/inventory/InventoryView.html#close()\"><code>InventoryView.close()</code></a>\n"
                  + "</ul>\n"
                  + "To invoke one of these methods, schedule a task using\n"
                  + "<a href=\"../../../../org/bukkit/scheduler/BukkitScheduler.html#runTask(org.bukkit.plugin.Plugin,%20java.lang.Runnable)\"><code>BukkitScheduler.runTask(Plugin, Runnable)</code></a>, which will run the task\n"
                  + "on the next tick. Also be aware that this is not an exhaustive list, and\n"
                  + "other methods could potentially create issues as well.\n"
                  + "<p>\n"
                  + "Assuming the EntityHuman associated with this event is an instance of a\n"
                  + "Player, manipulating the MaxStackSize or contents of an Inventory will\n"
                  + "require an Invocation of <a href=\"../../../../org/bukkit/entity/Player.html#updateInventory()\"><code>Player.updateInventory()</code></a>.\n"
                  + "<p>\n"
                  + "Any modifications to slots that are modified by the results of this\n"
                  + "InventoryDragEvent will be overwritten. To change these slots, this event\n"
                  + "should be cancelled and the changes applied. Alternatively, scheduling a\n"
                  + "task using <a href=\"../../../../org/bukkit/scheduler/BukkitScheduler.html#runTask(org.bukkit.plugin.Plugin,%20java.lang.Runnable)\"><code>BukkitScheduler.runTask(Plugin, Runnable)</code></a>, which would\n"
                  + "execute the task on the next tick, would work as well.</div>\n"
                  + "</li>\n"
                  + "</ul>";
        //        String code = "<code><b><i>HEY</i>BOLD</b></code> <input type=\"checkbox\" name=\"Kenntnisse_in\" value=\"HTML\" checked=\"checked\">";
        MapperCollection collection = new MapperCollection();
        for (StandardMappers standardMappers : StandardMappers.values()) {
            collection.addMapper(standardMappers);
        }

        HtmlConverter converter = new HtmlConverter(code, collection);
        converter.parse();
    }
}
