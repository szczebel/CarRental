package client.ui;

import client.ui.util.BackgroundOperation;
import client.ui.util.FilterableTable;
import common.domain.CurrentRental;
import common.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static client.ui.util.GuiHelper.convertingRenderer;
import static swingutils.components.ComponentFactory.*;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;

@Component
public class CurrentRentalsViewBuilder {

    @Autowired RentalService rentalService;

    public JComponent build() {
        CurrentRentalsTableModel tableModel = new CurrentRentalsTableModel();
        refresh(tableModel);
        FilterableTable ft = FilterableTable.create(tableModel);
        JTable table = ft.table;
        table.setDefaultRenderer(ZonedDateTime.class, convertingRenderer(value -> ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm"))));

        return borderLayout()
                .north(
                        flowLayout(
                                button("Refresh", () -> refresh(tableModel)),
                                label("Filter:"),
                                ft.filter,
                                button("Return selected", () -> returnClicked(table, tableModel))
                        ))
                .center(inScrollPane(table))
                .build();
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
