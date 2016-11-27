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
        String code = "<li class=\"blockList\"><a name=\"methods_inherited_from_class_org.bukkit.entity.Entity\">\n"
                  + " \n"
                  + "<h3>Methods inherited from interface&nbsp;org.bukkit.entity.<a href=\"../../../org/bukkit/entity/Entity.html\" title=\"interface in org.bukkit.entity\">Entity</a></h3>\n"
                  + "<code><a href=\"../../../org/bukkit/entity/Entity.html#addScoreboardTag(java.lang.String)\">addScoreboardTag</a>, <a href=\"../../../org/bukkit/entity/Entity.html#eject()\">eject</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getEntityId()\">getEntityId</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getFallDistance()\">getFallDistance</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getFireTicks()\">getFireTicks</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getLastDamageCause()\">getLastDamageCause</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getLocation()\">getLocation</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getLocation(org.bukkit.Location)\">getLocation</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getMaxFireTicks()\">getMaxFireTicks</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getNearbyEntities(double,%20double,%20double)\">getNearbyEntities</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getPassenger()\">getPassenger</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getPortalCooldown()\">getPortalCooldown</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getScoreboardTags()\">getScoreboardTags</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getServer()\">getServer</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getTicksLived()\">getTicksLived</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getType()\">getType</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getUniqueId()\">getUniqueId</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getVehicle()\">getVehicle</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getVelocity()\">getVelocity</a>, <a href=\"../../../org/bukkit/entity/Entity.html#getWorld()\">getWorld</a>, <a href=\"../../../org/bukkit/entity/Entity.html#hasGravity()\">hasGravity</a>, <a href=\"../../../org/bukkit/entity/Entity.html#isCustomNameVisible()\">isCustomNameVisible</a>, <a href=\"../../../org/bukkit/entity/Entity.html#isDead()\">isDead</a>, <a href=\"../../../org/bukkit/entity/Entity.html#isEmpty()\">isEmpty</a>, <a href=\"../../../org/bukkit/entity/Entity.html#isGlowing()\">isGlowing</a>, <a href=\"../../../org/bukkit/entity/Entity.html#isInsideVehicle()\">isInsideVehicle</a>, <a href=\"../../../org/bukkit/entity/Entity.html#isInvulnerable()\">isInvulnerable</a>, <a href=\"../../../org/bukkit/entity/Entity.html#isOnGround()\">isOnGround</a>, <a href=\"../../../org/bukkit/entity/Entity.html#isSilent()\">isSilent</a>, <a href=\"../../../org/bukkit/entity/Entity.html#isValid()\">isValid</a>, <a href=\"../../../org/bukkit/entity/Entity.html#leaveVehicle()\">leaveVehicle</a>, <a href=\"../../../org/bukkit/entity/Entity.html#playEffect(org.bukkit.EntityEffect)\">playEffect</a>, <a href=\"../../../org/bukkit/entity/Entity.html#remove()\">remove</a>, <a href=\"../../../org/bukkit/entity/Entity.html#removeScoreboardTag(java.lang.String)\">removeScoreboardTag</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setCustomNameVisible(boolean)\">setCustomNameVisible</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setFallDistance(float)\">setFallDistance</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setFireTicks(int)\">setFireTicks</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setGlowing(boolean)\">setGlowing</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setGravity(boolean)\">setGravity</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setInvulnerable(boolean)\">setInvulnerable</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setLastDamageCause(org.bukkit.event.entity.EntityDamageEvent)\">setLastDamageCause</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setPassenger(org.bukkit.entity.Entity)\">setPassenger</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setPortalCooldown(int)\">setPortalCooldown</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setSilent(boolean)\">setSilent</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setTicksLived(int)\">setTicksLived</a>, <a href=\"../../../org/bukkit/entity/Entity.html#setVelocity(org.bukkit.util.Vector)\">setVelocity</a>, <a href=\"../../../org/bukkit/entity/Entity.html#teleport(org.bukkit.entity.Entity)\">teleport</a>, <a href=\"../../../org/bukkit/entity/Entity.html#teleport(org.bukkit.entity.Entity,%20org.bukkit.event.player.PlayerTeleportEvent.TeleportCause)\">teleport</a>, <a href=\"../../../org/bukkit/entity/Entity.html#teleport(org.bukkit.Location)\">teleport</a>, <a href=\"../../../org/bukkit/entity/Entity.html#teleport(org.bukkit.Location,%20org.bukkit.event.player.PlayerTeleportEvent.TeleportCause)\">teleport</a></code></li>";
        //        String code = "<code><b><i>HEY</i>BOLD</b></code> <input type=\"checkbox\" name=\"Kenntnisse_in\" value=\"HTML\" checked=\"checked\">";
        MapperCollection collection = new MapperCollection();
        for (StandardMappers standardMappers : StandardMappers.values()) {
            collection.addMapper(standardMappers);
        }

        HtmlConverter converter = new HtmlConverter(code, collection);
        converter.parse();
    }
}
