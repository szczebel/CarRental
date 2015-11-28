package client.ui;

import common.service.RentalService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CurrentRentalsViewBuilder {

    private RentalService rentalService;

    public JComponent build() {

        CurrentRentalsTableModel tableModel = new CurrentRentalsTableModel();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildToolbar(tableModel), BorderLayout.NORTH);
        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(ZonedDateTime.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm")), isSelected, hasFocus, row, column);
            }
        });
        panel.add(new JScrollPane(table));

        return panel;
    }

    private JComponent buildToolbar(CurrentRentalsTableModel tableModel) {
        JPanel panel = new JPanel();

        panel.add(new JButton(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                BackgroundOperation.execute(
                        rentalService::getCurrentRentals,
                        tableModel::setData
                );
            }
        }));
        return panel;
    }


    @SuppressWarnings("unused")
    public void setRentalService(RentalService rentalService) {
        this.rentalService = rentalService;
    }
}
