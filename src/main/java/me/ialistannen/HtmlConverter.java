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
        String code = "<table class=\"overviewSummary\" summary=\"Method Summary table, listing methods, and an explanation\" cellspacing=\"0\" cellpadding=\"3\" border=\"0\">\n"
                  + "<caption><span>Methods</span><span class=\"tabEnd\">&nbsp;</span></caption>\n"
                  + "<tbody><tr>\n"
                  + "<th class=\"colFirst\" scope=\"col\">Modifier and Type</th>\n"
                  + "<th class=\"colLast\" scope=\"col\">Method and Description</th>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code>&lt;T&gt;&nbsp;<a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Future.html?is-external=true\" title=\"class or interface in java.util.concurrent\">Future</a>&lt;T&gt;</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#callSyncMethod(org.bukkit.plugin.Plugin,%20java.util.concurrent.Callable)\">callSyncMethod</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Callable.html?is-external=true\" title=\"class or interface in java.util.concurrent\">Callable</a>&lt;T&gt;&nbsp;task)</code>\n"
                  + "<div class=\"block\">Calls a method on the main thread and returns a Future object.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code>void</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#cancelAllTasks()\">cancelAllTasks</a></strong>()</code>\n"
                  + "<div class=\"block\">Removes all tasks from the scheduler.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code>void</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#cancelTask(int)\">cancelTask</a></strong>(int&nbsp;taskId)</code>\n"
                  + "<div class=\"block\">Removes task from scheduler.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code>void</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#cancelTasks(org.bukkit.plugin.Plugin)\">cancelTasks</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin)</code>\n"
                  + "<div class=\"block\">Removes all tasks associated with a particular plugin from the\n"
                  + "scheduler.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/List.html?is-external=true\" title=\"class or interface in java.util\">List</a>&lt;<a href=\"../../../org/bukkit/scheduler/BukkitWorker.html\" title=\"interface in org.bukkit.scheduler\">BukkitWorker</a>&gt;</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#getActiveWorkers()\">getActiveWorkers</a></strong>()</code>\n"
                  + "<div class=\"block\">Returns a list of all active workers.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/List.html?is-external=true\" title=\"class or interface in java.util\">List</a>&lt;<a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a>&gt;</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#getPendingTasks()\">getPendingTasks</a></strong>()</code>\n"
                  + "<div class=\"block\">Returns a list of all pending tasks.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code>boolean</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#isCurrentlyRunning(int)\">isCurrentlyRunning</a></strong>(int&nbsp;taskId)</code>\n"
                  + "<div class=\"block\">Check if the task currently running.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code>boolean</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#isQueued(int)\">isQueued</a></strong>(int&nbsp;taskId)</code>\n"
                  + "<div class=\"block\">Check if the task queued to be run later.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTask(org.bukkit.plugin.Plugin,%20org.bukkit.scheduler.BukkitRunnable)\">runTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html\" title=\"class in org.bukkit.scheduler\">BukkitRunnable</a>&nbsp;task)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>Use <a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html#runTask(org.bukkit.plugin.Plugin)\"><code>BukkitRunnable.runTask(Plugin)</code></a></i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTask(org.bukkit.plugin.Plugin,%20java.lang.Runnable)\">runTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task)</code>\n"
                  + "<div class=\"block\">Returns a task that will run on the next server tick.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTaskAsynchronously(org.bukkit.plugin.Plugin,%20org.bukkit.scheduler.BukkitRunnable)\">runTaskAsynchronously</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html\" title=\"class in org.bukkit.scheduler\">BukkitRunnable</a>&nbsp;task)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>Use <a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html#runTaskAsynchronously(org.bukkit.plugin.Plugin)\"><code>BukkitRunnable.runTaskAsynchronously(Plugin)</code></a></i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTaskAsynchronously(org.bukkit.plugin.Plugin,%20java.lang.Runnable)\">runTaskAsynchronously</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task)</code>\n"
                  + "<div class=\"block\"><b>Asynchronous tasks should never access any API in Bukkit.</b></div><b>\n"
                  + "</b></td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTaskLater(org.bukkit.plugin.Plugin,%20org.bukkit.scheduler.BukkitRunnable,%20long)\">runTaskLater</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html\" title=\"class in org.bukkit.scheduler\">BukkitRunnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>Use <a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html#runTaskLater(org.bukkit.plugin.Plugin,%20long)\"><code>BukkitRunnable.runTaskLater(Plugin, long)</code></a></i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTaskLater(org.bukkit.plugin.Plugin,%20java.lang.Runnable,%20long)\">runTaskLater</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay)</code>\n"
                  + "<div class=\"block\">Returns a task that will run after the specified number of server\n"
                  + "ticks.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTaskLaterAsynchronously(org.bukkit.plugin.Plugin,%20org.bukkit.scheduler.BukkitRunnable,%20long)\">runTaskLaterAsynchronously</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html\" title=\"class in org.bukkit.scheduler\">BukkitRunnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>Use <a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html#runTaskLaterAsynchronously(org.bukkit.plugin.Plugin,%20long)\"><code>BukkitRunnable.runTaskLaterAsynchronously(Plugin, long)</code></a></i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTaskLaterAsynchronously(org.bukkit.plugin.Plugin,%20java.lang.Runnable,%20long)\">runTaskLaterAsynchronously</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay)</code>\n"
                  + "<div class=\"block\"><b>Asynchronous tasks should never access any API in Bukkit.</b></div><b>\n"
                  + "</b></td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTaskTimer(org.bukkit.plugin.Plugin,%20org.bukkit.scheduler.BukkitRunnable,%20long,%20long)\">runTaskTimer</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html\" title=\"class in org.bukkit.scheduler\">BukkitRunnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay,\n"
                  + "long&nbsp;period)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>Use <a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html#runTaskTimer(org.bukkit.plugin.Plugin,%20long,%20long)\"><code>BukkitRunnable.runTaskTimer(Plugin, long, long)</code></a></i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTaskTimer(org.bukkit.plugin.Plugin,%20java.lang.Runnable,%20long,%20long)\">runTaskTimer</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay,\n"
                  + "long&nbsp;period)</code>\n"
                  + "<div class=\"block\">Returns a task that will repeatedly run until cancelled, starting after\n"
                  + "the specified number of server ticks.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTaskTimerAsynchronously(org.bukkit.plugin.Plugin,%20org.bukkit.scheduler.BukkitRunnable,%20long,%20long)\">runTaskTimerAsynchronously</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html\" title=\"class in org.bukkit.scheduler\">BukkitRunnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay,\n"
                  + "long&nbsp;period)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>Use <a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html#runTaskTimerAsynchronously(org.bukkit.plugin.Plugin,%20long,%20long)\"><code>BukkitRunnable.runTaskTimerAsynchronously(Plugin, long, long)</code></a></i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code><a href=\"../../../org/bukkit/scheduler/BukkitTask.html\" title=\"interface in org.bukkit.scheduler\">BukkitTask</a></code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#runTaskTimerAsynchronously(org.bukkit.plugin.Plugin,%20java.lang.Runnable,%20long,%20long)\">runTaskTimerAsynchronously</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay,\n"
                  + "long&nbsp;period)</code>\n"
                  + "<div class=\"block\"><b>Asynchronous tasks should never access any API in Bukkit.</b></div><b>\n"
                  + "</b></td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code>int</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#scheduleAsyncDelayedTask(org.bukkit.plugin.Plugin,%20java.lang.Runnable)\">scheduleAsyncDelayedTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>This name is misleading, as it does not schedule \"a sync\"\n"
                  + "task, but rather, \"an async\" task</i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code>int</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#scheduleAsyncDelayedTask(org.bukkit.plugin.Plugin,%20java.lang.Runnable,%20long)\">scheduleAsyncDelayedTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>This name is misleading, as it does not schedule \"a sync\"\n"
                  + "task, but rather, \"an async\" task</i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code>int</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#scheduleAsyncRepeatingTask(org.bukkit.plugin.Plugin,%20java.lang.Runnable,%20long,%20long)\">scheduleAsyncRepeatingTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay,\n"
                  + "long&nbsp;period)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>This name is misleading, as it does not schedule \"a sync\"\n"
                  + "task, but rather, \"an async\" task</i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code>int</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#scheduleSyncDelayedTask(org.bukkit.plugin.Plugin,%20org.bukkit.scheduler.BukkitRunnable)\">scheduleSyncDelayedTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html\" title=\"class in org.bukkit.scheduler\">BukkitRunnable</a>&nbsp;task)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>Use <a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html#runTask(org.bukkit.plugin.Plugin)\"><code>BukkitRunnable.runTask(Plugin)</code></a></i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code>int</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#scheduleSyncDelayedTask(org.bukkit.plugin.Plugin,%20org.bukkit.scheduler.BukkitRunnable,%20long)\">scheduleSyncDelayedTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html\" title=\"class in org.bukkit.scheduler\">BukkitRunnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>Use <a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html#runTaskLater(org.bukkit.plugin.Plugin,%20long)\"><code>BukkitRunnable.runTaskLater(Plugin, long)</code></a></i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code>int</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#scheduleSyncDelayedTask(org.bukkit.plugin.Plugin,%20java.lang.Runnable)\">scheduleSyncDelayedTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task)</code>\n"
                  + "<div class=\"block\">Schedules a once off task to occur as soon as possible.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code>int</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#scheduleSyncDelayedTask(org.bukkit.plugin.Plugin,%20java.lang.Runnable,%20long)\">scheduleSyncDelayedTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay)</code>\n"
                  + "<div class=\"block\">Schedules a once off task to occur after a delay.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"rowColor\">\n"
                  + "<td class=\"colFirst\"><code>int</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#scheduleSyncRepeatingTask(org.bukkit.plugin.Plugin,%20org.bukkit.scheduler.BukkitRunnable,%20long,%20long)\">scheduleSyncRepeatingTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html\" title=\"class in org.bukkit.scheduler\">BukkitRunnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay,\n"
                  + "long&nbsp;period)</code>\n"
                  + "<div class=\"block\"><strong>Deprecated.</strong>&nbsp;\n"
                  + "<div class=\"block\"><i>Use <a href=\"../../../org/bukkit/scheduler/BukkitRunnable.html#runTaskTimer(org.bukkit.plugin.Plugin,%20long,%20long)\"><code>BukkitRunnable.runTaskTimer(Plugin, long, long)</code></a> *</i></div>\n"
                  + "</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "<tr class=\"altColor\">\n"
                  + "<td class=\"colFirst\"><code>int</code></td>\n"
                  + "<td class=\"colLast\"><code><strong><a href=\"../../../org/bukkit/scheduler/BukkitScheduler.html#scheduleSyncRepeatingTask(org.bukkit.plugin.Plugin,%20java.lang.Runnable,%20long,%20long)\">scheduleSyncRepeatingTask</a></strong>(<a href=\"../../../org/bukkit/plugin/Plugin.html\" title=\"interface in org.bukkit.plugin\">Plugin</a>&nbsp;plugin,\n"
                  + "<a href=\"http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html?is-external=true\" title=\"class or interface in java.lang\">Runnable</a>&nbsp;task,\n"
                  + "long&nbsp;delay,\n"
                  + "long&nbsp;period)</code>\n"
                  + "<div class=\"block\">Schedules a repeating task.</div>\n"
                  + "</td>\n"
                  + "</tr>\n"
                  + "</tbody></table>";
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
