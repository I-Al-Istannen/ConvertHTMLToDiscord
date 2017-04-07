package me.ialistannen.htmltodiscord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import me.ialistannen.htmltodiscord.util.StringUtils;
import me.ialistannen.htmltodiscord.util.TableCreator;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * The standard mappers
 */
public enum StandardMappers implements Mapper {

    A_NAME("a", html -> html) {
        @Override
        public boolean matches(WrappedElement element) {
            return element.getWrapped().tagName().equalsIgnoreCase("a") && element.getWrapped().hasAttr("name");
        }
    },
    BOLD(html -> "**" + html + "**", "b", "strong"),
    ITALIC(html -> "_" + html + "_", "i", "em", "tt"),
    CITE("cite", ITALIC::convert),
    CHECKBOX("", html -> "") {
        @Override
        public boolean matches(WrappedElement element) {
            return element.getWrapped().attr("type").equalsIgnoreCase("checkbox");
        }

        @Override
        public String convert(String input, WrappedElement context) {
            return context.getWrapped().attr("checked").equalsIgnoreCase("checked") ? "[X]" : "[ ]";
        }
    },
    CODE((html) -> html, "code", "blockquote") {
        @Override
        public String convert(String input, WrappedElement context) {
            // skip code for links
            if (context.getWrapped().getElementsByTag("a").stream().anyMatch(element -> element.hasAttr("href"))) {
                return input;
            }
            if (input.contains("\n")) {
                String prefix = "\n```" + (input.startsWith("\n") ? "" : "\n");
                String suffix = (input.endsWith("\n") ? "" : "\n") + "```" + "\n";
                return prefix + input + suffix;
            }
            return "`" + input + "`";
        }
    },
    DESCRIPTION_LIST("dl", html -> "\n" + html),
    DESCRIPTION_TAG("dt", html -> html) {
        @Override
        public String convert(String input, WrappedElement context) {
            if (!context.getWrapped().getElementsByTag("b").isEmpty()
                      || !context.getWrapped().getElementsByTag("strong").isEmpty()
                      || context.getWrapped().getElementsByAttributeValue("class", "strong").stream().anyMatch(element -> element.tagName().equals("span"))) {
                return "\n" + input;
            }
            return "\n" + BOLD.convert(input);
        }
    },
    DESCRIPTION_DESCRIPTION("dd", html -> StringUtils.repeat(" ", 3) + html) {
        @Override
        public String convert(String input, WrappedElement context) {
            return "\n" + StringUtils.repeat(" ", 3) + input;
        }
    },
    DIV("div", html -> "\n" + html),
    FONT("font", html -> html),
    HORIZONTAL_LINE("hr", html -> StringUtils.repeat("-", 20) + html),
    HEADING("h", (html) -> "\n\n" + ITALIC.convert(BOLD.convert(html)) + "\n") {
        @Override
        public boolean matches(String htmlTag) {
            return htmlTag.matches("h[0-4]");
        }
    },
    IMAGE("img", html -> html),
    LINE_BREAK("br", html -> "\n" + html),
    LINK("a", html -> html) {
        @Override
        public boolean matches(WrappedElement element) {
            Element wrapped = element.getWrapped();
            return wrapped.tagName().equalsIgnoreCase("a") && wrapped.hasAttr("href");
        }

        @Override
        public String convert(String input, WrappedElement context) {
            Element wrapped = context.getWrapped();

            String target = wrapped.absUrl("href");
            String name = context.getConverterStorage().getReplacement(wrapped);

            if (wrapped.parent().tagName().equals("code")) {
                if (wrapped.children().size() > 1) {
                    return name;
                }
            }

            return "[" + name + "](" + target.replace(")", "\\)") + ")";
        }
    },
    LIST("ul", html -> html) {
        @Override
        public boolean matches(String htmlTag) {
            return htmlTag.equalsIgnoreCase("ul") || htmlTag.equalsIgnoreCase("ol");
        }

        @Override
        public String convert(String input, WrappedElement context) {
            StringBuilder builder = new StringBuilder();

            boolean ordered = context.getWrapped().tagName().equalsIgnoreCase("ol");

            Elements children = context.getWrapped().children();
            for (int i = 0; i < children.size(); i++) {
                Element element = children.get(i);
                String replaced = context.getConverterStorage().getReplacement(element);
                builder.append("\n");
                if (ordered) {
                    builder.append(Integer.toString(i + 1)).append(". ");
                } else {
                    builder.append(BOLD.convert("-")).append(" ");
                }
                builder.append(replaced);
            }

            builder.append("\n\n");
            return "\n" + builder.toString();
        }
    },
    LIST_ITEM("li", html -> html),
    PARAGRAPH("p", html -> "\n" + html) {
        @Override
        public String convert(String input, WrappedElement context) {
            if(context.getWrapped().parent().tagName().equalsIgnoreCase("li")) {
                return input;
            }
            return "\n" + input;
        }
    },
    PRE("pre", html -> html) {
        //        @Override
        //        public String convert(String input, WrappedElement context) {
        //            Element wrapped = context.getWrapped();
        //            String wrappedText = context.getConverterStorage().getReplacement(wrapped);
        //
        //            if (wrapped.getElementsByTag("code").isEmpty()) {
        //                return CODE.convert(wrappedText);
        //            }
        //
        //            return wrappedText;
        //        }
    },
    ROOT("root", (html) -> html),
    SPAN("span", html -> html) {
        @Override
        public boolean matches(WrappedElement element) {
            Element wrapped = element.getWrapped();
            return wrapped.tagName().equals("span")
                      && !(wrapped.hasAttr("class") && wrapped.attr("class").equalsIgnoreCase("strong"));
        }
    },
    SPAN_STRONG("span", BOLD::convert) {
        @Override
        public boolean matches(WrappedElement element) {
            Element wrapped = element.getWrapped();
            return wrapped.tagName().equals("span")
                      && wrapped.hasAttr("class")
                      && wrapped.attr("class").equalsIgnoreCase("strong");
        }
    },
    SUP("sup", html -> "^{" + html + "}"),
    SUB("sub", html -> "_{" + html + "}"),
    TABLE("table", html -> html) {
        @Override
        public String convert(String input, WrappedElement context) {
            Element wrapped = context.getWrapped();

            TableCreator tableCreator = new TableCreator(() -> " | ", 55);

            // loop through rows
            for (Element element : wrapped.getElementsByTag("tr")) {
                Elements cells = element.getElementsByTag("th");
                cells.addAll(element.getElementsByTag("td"));

                TableCreator.RowSeparator rowSeparator = length -> {
                    if (element.getElementsByTag("th").isEmpty()) {
                        return StringUtils.repeat("-", length);
                    }
                    return StringUtils.repeat("=", length);
                };

                Collection<TableCreator.Column> columns = new ArrayList<>();

                // cells
                //noinspection Convert2streamapi
                for (Element cell : cells) {
                    columns.add(() -> getText(cell));
                }

                tableCreator.addLine(rowSeparator, columns);
            }

            return "\n```\n" + tableCreator.build().print() + "\n```\n";
        }

        String getText(Element parentElement) {
            String working = "";
            for (Node child : parentElement.childNodes()) {
                if (child instanceof TextNode) {
                    working += ((TextNode) child).getWholeText();
                }
                if (child instanceof Element) {
                    Element childElement = (Element) child;
                    // do more of these for p or other tags you want a new line for
                    if (childElement.tag().getName().equalsIgnoreCase("br")) {
                        working += "\n";
                    }
                    working += getText(childElement);
                }
            }

            return working;
        }
    },
    TABLE_BODY("tbody", html -> html) {
        @Override
        public String convert(String input, WrappedElement context) {
            StringBuilder builder = new StringBuilder();

            Element wrapped = context.getWrapped();

            for (Element element : wrapped.children()) {
                String replacement = context.getConverterStorage().getReplacement(element);
                builder.append(replacement);
            }

            return builder.toString();
        }
    },
    TABLE_CAPTION("caption", html -> HEADING.convert(html).replace("\n", "") + "\n") {
        @Override
        public boolean matches(WrappedElement element) {
            Element wrapped = element.getWrapped();
            return wrapped.tagName().equalsIgnoreCase("caption") && wrapped.parent().tagName().equals("table");
        }
    },
    TABLE_CELL("td", html -> html),
    TABLE_HEADING("th", HEADING::convert),
    TABLE_ROW("tr", html -> html),
    UNDERLINE("u", html -> "___" + html + "___"),
    VAR("var", ITALIC::convert);

    private Predicate<String>        htmlIdentifier;
    private Function<String, String> converter;

    StandardMappers(Predicate<String> htmlIdentifier, Function<String, String> converter) {
        this.htmlIdentifier = htmlIdentifier;
        this.converter = converter;
    }

    StandardMappers(String tag, Function<String, String> converter) {
        this(s -> s.equalsIgnoreCase(tag), converter);
    }

    StandardMappers(Function<String, String> converter, String... tags) {
        this(s -> Arrays.stream(tags).map(String::toLowerCase).anyMatch(s1 -> s1.equalsIgnoreCase(s)), converter);
    }

    @Override
    public boolean matches(String htmlTag) {
        return htmlIdentifier.test(htmlTag);
    }

    @Override
    public String convert(String input) {
        return converter.apply(input);
    }
}
