package client.ui;

import common.domain.CurrentRental;
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
        JTable table = new JTable(tableModel);
        panel.add(buildToolbar(tableModel, table), BorderLayout.NORTH);
        table.setDefaultRenderer(ZonedDateTime.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm")), isSelected, hasFocus, row, column);
            }
        });
        panel.add(new JScrollPane(table));
        refresh(tableModel);
        return panel;
    }

    private JComponent buildToolbar(CurrentRentalsTableModel tableModel, JTable table) {
        JPanel panel = new JPanel();

        panel.add(new JButton(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh(tableModel);
            }
        }));

        panel.add(new JButton(new AbstractAction("Return") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    CurrentRental r = tableModel.getAt(table.convertRowIndexToModel(selectedRow));
                    BackgroundOperation.execute(
                            () -> rentalService.returnCar(r.getRegistration()),
                            () -> refresh(tableModel)
                    );
                }
            }
        }));
        return panel;
    }

    private void refresh(CurrentRentalsTableModel tableModel) {
        BackgroundOperation.execute(
                rentalService::getCurrentRentals,
                tableModel::setData
        );
    }


    @SuppressWarnings("unused")
    public void setRentalService(RentalService rentalService) {
        this.rentalService = rentalService;
    }
}
