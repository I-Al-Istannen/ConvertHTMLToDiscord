package me.ialistannen.htmltodiscord.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A Table
 */
public class TableCreator {

  /**
   * Can't use a Map, as the HashCode of Row can NOT be guaranteed to be stable
   */
  private List<Line> lines;
  private ColumnSeparator columnSeparator;
  private int maxWidth;

  /**
   * @param columnSeparator The column separator
   * @param maxWidth The maximum width for the whole table
   */
  public TableCreator(ColumnSeparator columnSeparator, int maxWidth) {
    this.columnSeparator = columnSeparator;
    this.maxWidth = maxWidth;

    lines = new ArrayList<>();
  }

  /**
   * Adds a Line to the table
   *
   * @param rowSeparator The {@link RowSeparator} for this line
   * @param columns The columns to add
   * @return This {@link TableCreator}
   */
  public TableCreator addLine(RowSeparator rowSeparator, Collection<Column> columns) {
    lines.add(new Line(columns, rowSeparator));
    return this;
  }

  /**
   * Adds a Line to the table. Uses the last row or a new default one
   *
   * @param rowSeparator The {@link RowSeparator} for this line
   * @param columns The columns to add
   * @return This {@link TableCreator}
   * @see #addLine(RowSeparator, Collection)
   */
  public TableCreator addLine(RowSeparator rowSeparator, Column... columns) {
    return addLine(rowSeparator, Arrays.asList(columns));
  }

  /**
   * @return The maximum width of this table
   */
  public int getMaxWidth() {
    return maxWidth;
  }

  /**
   * Builds the table
   *
   * @return The resulting table
   */
  public me.ialistannen.htmltodiscord.util.Table build() {
    return new me.ialistannen.htmltodiscord.util.Table(this);
  }

  /**
   * Returns all lines in the table
   *
   * @return All {@link Line}s in the table. Unmodifiable.
   */
  List<Line> getLines() {
    return Collections.unmodifiableList(lines);
  }

  /**
   * @return The {@link ColumnSeparator}
   */
  ColumnSeparator getColumnSeparator() {
    return columnSeparator;
  }

  /**
   * A line in the table
   * <p>
   * Can't use a Map, as the HashCode of Row can NOT be guaranteed to be stable
   */
  static class Line {

    private List<Column> columns;
    private RowSeparator rowSeparator;

    /**
     * @param columns The columns in the row
     * @param rowSeparator The {@link RowSeparator} for this line
     */
    Line(Collection<Column> columns, RowSeparator rowSeparator) {
      this.columns = new ArrayList<>(columns);
      this.rowSeparator = rowSeparator;
    }

    /**
     * @return The columns. Unmodifiable
     */
    List<Column> getColumns() {
      return Collections.unmodifiableList(columns);
    }

    /**
     * @return The {@link RowSeparator}
     */
    RowSeparator getRowSeparator() {
      return rowSeparator;
    }

    /**
     * @param column The column to add
     */
    void addColumn(Column column) {
      columns.add(column);
    }
  }

  /**
   * A table column
   */
  @FunctionalInterface
  public interface Column {

    /**
     * @return The column
     */
    String getColumn();
  }

  /**
   * A column separator
   */
  @FunctionalInterface
  public interface ColumnSeparator {

    /**
     * @return The Separator. Must have a consistent length
     */
    String getSeparator();
  }

  /**
   * A row separator
   */
  @FunctionalInterface
  public interface RowSeparator {

    /**
     * Returns the separator
     *
     * @param length The length of the separator
     * @return The separator
     */
    String getSeparator(int length);
  }
}
