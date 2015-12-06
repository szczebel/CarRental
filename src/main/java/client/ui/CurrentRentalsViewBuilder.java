package client.ui;

import common.domain.CurrentRental;
import common.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static client.ui.util.GuiHelper.*;

@Component
public class CurrentRentalsViewBuilder {

    @Autowired RentalService rentalService;

    public JComponent build() {

        CurrentRentalsTableModel tableModel = new CurrentRentalsTableModel();
        refresh(tableModel);
        JTable table = new JTable(tableModel);
        configureRenderer(table);

        return borderLayout()
                .north(
                        toolbar(
                                button("Refresh", () -> refresh(tableModel)),
                                button("Return", () -> returnClicked(table, tableModel))
                        ))
                .center(inScrollPane(table))
                .build();
    }


    private void configureRenderer(JTable table) {
        table.setDefaultRenderer(ZonedDateTime.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm")), isSelected, hasFocus, row, column);
            }
        });
    }

    private void returnClicked(JTable table, CurrentRentalsTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            CurrentRental r = tableModel.getAt(table.convertRowIndexToModel(selectedRow));
            BackgroundOperation.execute(
                    () -> rentalService.returnCar(r.getRegistration()),
                    () -> refresh(tableModel)
            );
        } else {
            JOptionPane.showMessageDialog(table, "Please select a car being returned");
        }
    }

    private void refresh(CurrentRentalsTableModel tableModel) {
        BackgroundOperation.execute(
                rentalService::getCurrentRentals,
                tableModel::setData
        );
    }
}
