package me.ialistannen;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The standard mappers
 */
public enum StandardMappers implements Mapper {

    ROOT("root", (html) -> html),
    BOLD(html -> "**" + html + "**", "b", "strong"),
    ITALIC("i", html -> "_" + html + "_"),
    LIST_ITEM("li", html -> html),
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
                    builder.append("- ");
                }
                builder.append(replaced);
            }

            builder.append("\n\n");
            return builder.toString();
        }
    },
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
    PARAGRAPH("p", html -> "\n" + html),
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

            // just links in code
            for (Element element : wrapped.parents()) {
                if (element.tagName().equalsIgnoreCase("code")) {
                    return name;
                }
            }

            return "[" + name + "](" + target + ")";
        }
    },
    A_NAME("a", html -> html) {
        @Override
        public boolean matches(WrappedElement element) {
            return element.getWrapped().tagName().equalsIgnoreCase("a") && element.getWrapped().hasAttr("name");
        }
    },
    DIV("div", html -> "\n" + html),
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
    CODE("code", (html) -> {
        if (html.contains("\n")) {
            return "\n```\n" + html + "\n```\n";
        }
        return "`" + html + "`";
    }),
    HEADING("h", (html) -> ITALIC.convert(BOLD.convert(html))) {
        @Override
        public boolean matches(String htmlTag) {
            return htmlTag.matches("h[0-4]");
        }
    },
    SPAN("span", html -> html),
    DESCRIPTION_LIST("dl", html -> html),
    DESCRIPTION_TAG("dt", html -> html) {
        @Override
        public String convert(String input, WrappedElement context) {
            if (!context.getWrapped().getElementsByTag("b").isEmpty()
                      || !context.getWrapped().getElementsByTag("strong").isEmpty()) {
                return input;
            }
            return BOLD.convert(input);
        }
    },
    DESCRIPTION_DESCRIPTION("dd", html -> repeat(" ", 3) + html) {
        @Override
        public String convert(String input, WrappedElement context) {
            return repeat(" ", 3) + input;
        }
    },
    LINE_BREAK("br", html -> "\n" + html),
    HORIZONTAL_LINE("hr", html -> repeat("-", 20) + html);

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

    private static String repeat(String string, int amount) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            builder.append(string);
        }

        return builder.toString();
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
