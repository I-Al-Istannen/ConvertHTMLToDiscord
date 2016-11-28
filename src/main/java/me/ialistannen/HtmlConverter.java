package me.ialistannen;

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

        System.out.println(flatten.stream()
                  .map(wrappedElement -> wrappedElement.getWrapped().tagName() + " '" + wrappedElement.getReplacedContent() + "'")
                  .collect(Collectors.counting()));
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

        System.out.println(outputQueue.stream().map(WrappedElement::getWrapped).map(Element::tagName).collect(Collectors.toList()));

        return new ArrayList<>(outputQueue);
    }

    /**
     * Some test code
     *
     * @param args The VM args
     */
    public static void main(String[] args) {
        String code = "<table class=\"overviewSummary\" summary=\"Packages table, listing packages, and an explanation\" cellspacing=\"0\" cellpadding=\"3\" border=\"0\">\n"
                  + "<caption><span>Packages</span><span class=\"tabEnd\">&nbsp;</span></caption>\n"
                  + "<tbody><tr>\n"
                  + "<th class=\"colFirst\" scope=\"col\">Package</th>\n"
                  + "<th class=\"colLast\" scope=\"col\">Description</th>\n"
                  + "</tr>\n"
                  + "</tbody><tbody>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/package-summary.html\">org.bukkit</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">More generalized classes in the API.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/attribute/package-summary.html\">org.bukkit.attribute</a></td>\n"
                  + "<td class=\"colLast\">&nbsp;</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/block/package-summary.html\">org.bukkit.block</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes used to manipulate the voxels in a <a href=\"org/bukkit/World.html\" title=\"interface in org.bukkit\"><code>world</code></a>,\n"
                  + "including special states.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/block/banner/package-summary.html\">org.bukkit.block.banner</a></td>\n"
                  + "<td class=\"colLast\">&nbsp;</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/boss/package-summary.html\">org.bukkit.boss</a></td>\n"
                  + "<td class=\"colLast\">&nbsp;</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/command/package-summary.html\">org.bukkit.command</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes relating to handling specialized non-chat player input.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/command/defaults/package-summary.html\">org.bukkit.command.defaults</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Commands for emulating the Minecraft commands and other necessary ones for\n"
                  + "use by a Bukkit implementation.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/configuration/package-summary.html\">org.bukkit.configuration</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes dedicated to handling a plugin's runtime configuration.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/configuration/file/package-summary.html\">org.bukkit.configuration.file</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes dedicated facilitating <a href=\"org/bukkit/configuration/Configuration.html\" title=\"interface in org.bukkit.configuration\"><code>configurations</code></a> to be read and\n"
                  + "stored on the filesystem.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/configuration/serialization/package-summary.html\">org.bukkit.configuration.serialization</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes dedicated to being able to perform serialization specialized for\n"
                  + "the Bukkit <a href=\"org/bukkit/configuration/Configuration.html\" title=\"interface in org.bukkit.configuration\"><code>configuration</code></a>\n"
                  + "implementation.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/conversations/package-summary.html\">org.bukkit.conversations</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes dedicated to facilitate direct player-to-plugin communication.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/enchantments/package-summary.html\">org.bukkit.enchantments</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes relating to the specialized enhancements to <a href=\"org/bukkit/inventory/ItemStack.html\" title=\"class in org.bukkit.inventory\"><code>item stacks</code></a>, as part of the <a href=\"org/bukkit/inventory/meta/ItemMeta.html\" title=\"interface in org.bukkit.inventory.meta\"><code>meta data</code></a>.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/entity/package-summary.html\">org.bukkit.entity</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Interfaces for non-voxel objects that can exist in a <a href=\"org/bukkit/World.html\" title=\"interface in org.bukkit\"><code>world</code></a>, including all players, monsters, projectiles, etc.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/entity/minecart/package-summary.html\">org.bukkit.entity.minecart</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Interfaces for various <a href=\"org/bukkit/entity/Minecart.html\" title=\"interface in org.bukkit.entity\"><code>Minecart</code></a> types.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/package-summary.html\">org.bukkit.event</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes dedicated to handling triggered code executions.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/block/package-summary.html\">org.bukkit.event.block</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> relating to when a <a href=\"org/bukkit/block/Block.html\" title=\"interface in org.bukkit.block\"><code>block</code></a> is changed or interacts with the <a href=\"org/bukkit/World.html\" title=\"interface in org.bukkit\"><code>world</code></a>.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/enchantment/package-summary.html\">org.bukkit.event.enchantment</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> triggered from an <a href=\"org/bukkit/inventory/EnchantingInventory.html\" title=\"interface in org.bukkit.inventory\"><code>enchantment table</code></a>.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/entity/package-summary.html\">org.bukkit.event.entity</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> relating to <a href=\"org/bukkit/entity/Entity.html\" title=\"interface in org.bukkit.entity\"><code>entities</code></a>, excluding some directly referencing\n"
                  + "some more specific entity types.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/hanging/package-summary.html\">org.bukkit.event.hanging</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> relating to <a href=\"org/bukkit/entity/Hanging.html\" title=\"interface in org.bukkit.entity\"><code>entities that hang</code></a>.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/inventory/package-summary.html\">org.bukkit.event.inventory</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> relating to <a href=\"org/bukkit/inventory/Inventory.html\" title=\"interface in org.bukkit.inventory\"><code>inventory</code></a> manipulation.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/painting/package-summary.html\">org.bukkit.event.painting</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> relating to <a href=\"org/bukkit/entity/Painting.html\" title=\"interface in org.bukkit.entity\"><code>paintings</code></a>, but deprecated for more general\n"
                  + "<a href=\"org/bukkit/event/hanging/package-summary.html\"><code>hanging</code></a> events.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/player/package-summary.html\">org.bukkit.event.player</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> relating to <a href=\"org/bukkit/entity/Player.html\" title=\"interface in org.bukkit.entity\"><code>players</code></a>.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/server/package-summary.html\">org.bukkit.event.server</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> relating to programmatic state\n"
                  + "changes on the server.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/vehicle/package-summary.html\">org.bukkit.event.vehicle</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> relating to <a href=\"org/bukkit/entity/Vehicle.html\" title=\"interface in org.bukkit.entity\"><code>vehicular entities</code></a>.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/weather/package-summary.html\">org.bukkit.event.weather</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> relating to weather.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/event/world/package-summary.html\">org.bukkit.event.world</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\"><a href=\"org/bukkit/event/Event.html\" title=\"class in org.bukkit.event\"><code>Events</code></a> triggered by various <a href=\"org/bukkit/World.html\" title=\"interface in org.bukkit\"><code>world</code></a> states or changes.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/generator/package-summary.html\">org.bukkit.generator</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes to facilitate <a href=\"org/bukkit/World.html\" title=\"interface in org.bukkit\"><code>world</code></a> generation\n"
                  + "implementation.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/help/package-summary.html\">org.bukkit.help</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes used to manipulate the default command and topic assistance system.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/inventory/package-summary.html\">org.bukkit.inventory</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes involved in manipulating player inventories and item interactions.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/inventory/meta/package-summary.html\">org.bukkit.inventory.meta</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">The interfaces used when manipulating extra data can can be stored inside\n"
                  + "<a href=\"org/bukkit/inventory/ItemStack.html\" title=\"class in org.bukkit.inventory\"><code>item stacks</code></a>.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/map/package-summary.html\">org.bukkit.map</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes to facilitate plugin handling of <a href=\"org/bukkit/Material.html#MAP\"><code>map</code></a> displays.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/material/package-summary.html\">org.bukkit.material</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes that represents various voxel types and states.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/material/types/package-summary.html\">org.bukkit.material.types</a></td>\n"
                  + "<td class=\"colLast\">&nbsp;</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/metadata/package-summary.html\">org.bukkit.metadata</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes dedicated to providing a layer of plugin specified data on various\n"
                  + "Minecraft concepts.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/permissions/package-summary.html\">org.bukkit.permissions</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes dedicated to providing binary state properties to players.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/plugin/package-summary.html\">org.bukkit.plugin</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes specifically relating to loading software modules at runtime.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/plugin/java/package-summary.html\">org.bukkit.plugin.java</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes for handling <a href=\"org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\"><code>plugins</code></a> written in\n"
                  + "java.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/plugin/messaging/package-summary.html\">org.bukkit.plugin.messaging</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes dedicated to specialized plugin to client protocols.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/potion/package-summary.html\">org.bukkit.potion</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes to represent various <a href=\"org/bukkit/Material.html#POTION\"><code>potion</code></a>\n"
                  + "properties and manipulation.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/projectiles/package-summary.html\">org.bukkit.projectiles</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes to represent the source of a projectile</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/scheduler/package-summary.html\">org.bukkit.scheduler</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes dedicated to letting <a href=\"org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\"><code>plugins</code></a> run\n"
                  + "code at specific time intervals, including thread safety.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/scoreboard/package-summary.html\">org.bukkit.scoreboard</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Interfaces used to manage the client side score display system.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/util/package-summary.html\">org.bukkit.util</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Multi and single purpose classes to facilitate various programmatic\n"
                  + "concepts.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/util/io/package-summary.html\">org.bukkit.util.io</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes used to facilitate stream processing for specific Bukkit concepts.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/util/noise/package-summary.html\">org.bukkit.util.noise</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Classes dedicated to facilitating deterministic noise.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><a href=\"org/bukkit/util/permissions/package-summary.html\">org.bukkit.util.permissions</a></td>\n"
                  + "<td class=\"colLast\">\n"
                  + "<div class=\"block\">Static methods for miscellaneous <a href=\"org/bukkit/permissions/Permission.html\" title=\"class in org.bukkit.permissions\"><code>permission</code></a> functionality.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "</tbody>\n"
                  + "</table>";
        //        String code = "<code><b><i>HEY</i>BOLD</b></code> <input type=\"checkbox\" name=\"Kenntnisse_in\" value=\"HTML\" checked=\"checked\">";
        MapperCollection collection = new MapperCollection();
        for (StandardMappers standardMappers : StandardMappers.values()) {
            collection.addMapper(standardMappers);
        }

        HtmlConverter converter = new HtmlConverter(code, collection);
        String result = converter.parse("https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html");
        System.out.println(result);
    }
}
