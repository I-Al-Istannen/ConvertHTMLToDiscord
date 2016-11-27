package me.ialistannen;

import java.util.function.Function;
import java.util.function.Predicate;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The standard mappers
 */
public enum StandardMappers implements Mapper {

    ROOT("root", (html) -> html),
    BOLD("b", html -> "**" + html + "**"),
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
            if(wrapped.tagName().equalsIgnoreCase("a")) {
                System.out.println("Found a " + wrapped);
            }
            return wrapped.tagName().equalsIgnoreCase("a") && wrapped.hasAttr("href");
        }

        @Override
        public String convert(String input, WrappedElement context) {
            Element wrapped = context.getWrapped();

            
            String target = wrapped.absUrl("href");
            String name = context.getConverterStorage().getReplacement(wrapped);

            // just links in code
            for (Element element : wrapped.parents()) {
                if(element.tagName().equalsIgnoreCase("code")) {
                    return name;
                }
            }

            return "[" + name + "](" + target + ")";
        }
    },
    DIV("div", html -> "\n\n" + html + "\n\n"),
    PRE("pre", html -> html) {
        @Override
        public String convert(String input, WrappedElement context) {
            Element wrapped = context.getWrapped();
            String wrappedText = context.getConverterStorage().getReplacement(wrapped);

            if (wrapped.getElementsByTag("code").isEmpty()) {
                return CODE.convert(wrappedText);
            }

            return wrappedText;
        }
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
    DESCRIPTION_TAG("dt", html -> BOLD.convert(html) + "\n"),
    DESCRIPTION_DESCRIPTION("dd", html -> "\t" + html);

    private Predicate<String>        htmlIdentifier;
    private Function<String, String> converter;

    StandardMappers(Predicate<String> htmlIdentifier, Function<String, String> converter) {
        this.htmlIdentifier = htmlIdentifier;
        this.converter = converter;
    }

    StandardMappers(String tag, Function<String, String> converter) {
        this(s -> s.equalsIgnoreCase(tag), converter);
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
