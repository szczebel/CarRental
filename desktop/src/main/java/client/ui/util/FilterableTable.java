package client.ui.util;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import static client.ui.util.GuiHelper.textField;

public class FilterableTable {

    public final JTextField filter;
    public final JTable table;

    public FilterableTable(JTextField filter, JTable table) {
        this.filter = filter;
        this.table = table;
    }

    public static FilterableTable create(TableModel model) {
        JTable table = new JTable(model);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        JTextField filter = textField(10, s -> sorter.setRowFilter(RowFilter.regexFilter("(?i)" + s)));
        return new FilterableTable(filter, table);
    }
}
