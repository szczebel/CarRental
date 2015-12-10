package client.ui.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

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
        table.setRowSorter (sorter);

        JTextField filter = new JTextField(20);
        filter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            void onChange() {
                sorter.setRowFilter(RowFilter.regexFilter(filter.getText().trim()));
            }
        });

        return new FilterableTable(filter, table);
    }
}
