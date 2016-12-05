package me.ialistannen.htmltodiscord.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * A table
 */
public class Table {

    private List<SlicedLine>             slicedLines;
    private TableCreator.ColumnSeparator columnSeparator;
    private int                          maxWidth;

    /**
     * @param creator The {@link TableCreator} to use
     */
    Table(TableCreator creator) {
        maxWidth = creator.getMaxWidth();
        columnSeparator = creator.getColumnSeparator();

        List<TableCreator.Line> lines = new ArrayList<>(creator.getLines());

        fillLinesWithEmptyColumns(lines);

        slicedLines = sliceColumns(lines);
    }

    /**
     * Prints this table
     *
     * @return The printed table
     */
    public String print() {
        StringBuilder builder = new StringBuilder();

        for (SlicedLine slicedLine : slicedLines) {
            String line = slicedLine.print(columnSeparator);
            builder.append(line).append("\n");
        }

        return builder.toString();
    }

    /**
     * Brings all lines on the same amount of columns
     */
    private void fillLinesWithEmptyColumns(List<TableCreator.Line> lines) {
        int maxColumns = lines.stream().mapToInt(line -> line.getColumns().size()).max().orElse(-1);
        if (maxColumns == -1) {
            throw new IllegalArgumentException("No columns found!");
        }

        lines.forEach(line -> {
            if (line.getColumns().size() == maxColumns) {
                return;
            }

            int diff = maxColumns - line.getColumns().size();

            for (int i = 0; i < diff; i++) {
                line.addColumn(() -> "");
            }
        });
    }

    /**
     * Slices the lines in ones that fir in the width constraints
     *
     * @param lines The lines to slice
     *
     * @return The sliced lines
     */
    private List<SlicedLine> sliceColumns(List<TableCreator.Line> lines) {
        List<Integer> columnWidths = calculateColumnWidths(lines);
        List<SlicedLine> slicedLines = new ArrayList<>(lines.size());

        for (TableCreator.Line line : lines) {
            List<SlicedColumn> slicedColumns = new ArrayList<>();

            List<TableCreator.Column> columns = line.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                TableCreator.Column column = columns.get(i);
                SlicedColumn sliced = new SlicedColumn(column, columnWidths.get(i));
                slicedColumns.add(sliced);
            }

            slicedLines.add(new SlicedLine(slicedColumns, line.getRowSeparator()));
        }

        return slicedLines;
    }

    /**
     * @return The calculated column sizes
     */
    private List<Integer> calculateColumnWidths(List<TableCreator.Line> lines) {
        List<Integer> columnWidths = new ArrayList<>();
        int averageWidth = maxWidth;
        averageWidth -= (lines.get(0).getColumns().size() + 1) * columnSeparator.getSeparator().length();
        averageWidth /= lines.get(0).getColumns().size();

        for (TableCreator.Line line : lines) {
            List<TableCreator.Column> lineColumns = line.getColumns();
            for (int i = 0; i < lineColumns.size(); i++) {
                // + 2 because it seemed to work. Too lazy to figure out why it is needed.
                int width = lineColumns.get(i).getColumn().length() + 2;

                if (columnWidths.size() - 1 < i) {
                    columnWidths.add(-1);
                }

                if (averageWidth <= 0) {
                    if (columnWidths.get(i) < width) {
                        columnWidths.set(i, width);
                    }
                } else {
                    if (width < averageWidth) {
                        if (columnWidths.get(i) <= width) {
                            columnWidths.set(i, width);
                        }
                    } else {
                        columnWidths.set(i, averageWidth);
                    }
                }
            }
        }

        int pool = maxWidth - columnWidths.stream().mapToInt(Integer::intValue).sum();
        pool -= (columnWidths.size() + 1) * columnSeparator.getSeparator().length();

        List<Integer> maxWidths = getMaxWidth(lines);

        if (pool > 0) {
            for (int i = 0; i < columnWidths.size(); i++) {
                int columnWidth = columnWidths.get(i);

                if (columnWidth < maxWidths.get(i)) {
                    if (pool <= 0) {
                        continue;
                    }
                    int newWidth = Math.min(columnWidth + pool, maxWidths.get(i));
                    pool -= newWidth - columnWidth;

                    columnWidths.set(i, newWidth);
                } else {
                    pool += maxWidths.get(i) - columnWidth;
                }
            }
        }

        return columnWidths;
    }

    private List<Integer> getMaxWidth(List<TableCreator.Line> lines) {
        List<Integer> width = new ArrayList<>();

        for (TableCreator.Line line : lines) {
            List<TableCreator.Column> columns = line.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                TableCreator.Column column = columns.get(i);

                int columnWidth = column.getColumn().length();

                if (width.size() - 1 < i) {
                    width.add(columnWidth);
                } else if (width.get(i) < columnWidth) {
                    width.set(i, columnWidth);
                }
            }
        }

        return width;
    }

    /**
     * A sliced line
     */
    private static class SlicedLine {
        private List<SlicedColumn>        columns;
        private TableCreator.RowSeparator rowSeparator;

        /**
         * @param columns The columns
         * @param rowSeparator The row separator
         */
        private SlicedLine(List<SlicedColumn> columns, TableCreator.RowSeparator rowSeparator) {
            this.columns = columns;
            this.rowSeparator = rowSeparator;
        }

        /**
         * @param columnSeparator The {@link TableCreator.ColumnSeparator}
         *
         * @return The row with the separator
         */
        private String print(TableCreator.ColumnSeparator columnSeparator) {
            StringBuilder builder = new StringBuilder();
            int maxLines = columns.stream().mapToInt(column -> column.getLines().size()).max().orElse(0);

            for (int i = 0; i < maxLines; i++) {
                builder.append(columnSeparator.getSeparator());
                for (SlicedColumn column : columns) {
                    if (column.getLines().size() - 1 < i) {
                        builder.append(me.ialistannen.htmltodiscord.util.StringUtils.repeat(" ", column.getLength()));
                    } else {
                        String line = column.getLines().get(i);
                        builder.append(me.ialistannen.htmltodiscord.util.StringUtils.padToLength(line, ' ', column.getLength()));
                    }
                    builder.append(columnSeparator.getSeparator());
                }
                builder.append("\n");
            }

            int length = columns.stream().mapToInt(SlicedColumn::getLength).sum();
            length += (columns.size() + 1) * columnSeparator.getSeparator().length();
            builder.append(rowSeparator.getSeparator(length));

            return builder.toString();
        }
    }

    /**
     * A column that was sliced into it's place
     */
    private static class SlicedColumn {
        private List<String> lines;
        private int          length;

        /**
         * @param column The column to use
         * @param length The max length of a line
         */
        private SlicedColumn(TableCreator.Column column, int length) {
            this.length = length;

            lines = new ArrayList<>(trimToLength(column.getColumn(), length));
        }

        /**
         * Trims the text to a given length
         *
         * @param text The text to trim
         * @param maxLength The max length of the lore
         *
         * @return The trimmed text
         */
        private List<String> trimToLength(String text, int maxLength) {
            Objects.requireNonNull(text, "text can not be null!");

            if (maxLength < 1) {
                throw new IllegalArgumentException("MaxLength must be > 0");
            }

            if (!text.contains(" ")) {
                return Collections.singletonList(trimToLengthWithEllipsis(text, maxLength));
            }

            List<String> newLore = new LinkedList<>();
            Queue<String> parts = new LinkedList<>(Arrays.asList(text.trim().replace("\n", "!!--!! ").split(" ")));
            String result = "";
            for (String tmp : parts) {
                String withoutLineBreak = tmp.replace("!!--!!", "");
                if (result.length() + withoutLineBreak.length() < maxLength) {
                    result += withoutLineBreak + " ";
                    if (tmp.contains("!!--!!") && !tmp.startsWith("!!--!!")) {
                        newLore.add(result);
                        result = "";
                    }
                } else {
                    result = trimToLengthWithEllipsis(result, maxLength);

                    newLore.add(result);
                    result = withoutLineBreak + " ";

                    result = trimToLengthWithEllipsis(result, maxLength);

                    if (tmp.contains("!!--!!") && !tmp.startsWith("!!--!!")) {
                        newLore.add(result);
                        result = "";
                    }
                }
            }
            newLore.add(result.substring(0, Math.min(maxLength, result.length())));

            return newLore;
        }

        private String trimToLengthWithEllipsis(String string, int maxLength) {
            if (maxLength < 4) {
                return string.substring(0, Math.min(string.length(), maxLength));
            }
            if (string.length() <= maxLength) {
                return string;
            }

            return string.substring(0, maxLength - 3) + "...";
        }

        /**
         * @return The length of the column
         */
        private int getLength() {
            return length;
        }

        /**
         * @return The lines
         */
        private List<String> getLines() {
            return lines;
        }
    }
}
