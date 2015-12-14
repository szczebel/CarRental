package client.ui;

import client.ui.util.BackgroundOperation;
import common.domain.Car;
import common.domain.Client;
import common.domain.RentalClass;
import common.service.AvailabilityService;
import common.service.ClientService;
import common.service.RentalService;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static client.ui.util.GuiHelper.datePicker;
import static client.ui.util.GuiHelper.rentalClassChooser;
import static common.util.TimeUtils.toMidnight;
import static swingutils.components.ComponentFactory.*;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;
import static swingutils.layout.forms.FormLayoutBuilders.simpleForm;

@Component
public class MakeARentViewBuilder {

    @Autowired    AvailabilityService availabilityService;
    @Autowired    RentalService rentalService;
    @Autowired    ClientService clientService;
    @Autowired    RentalClasses rentalClasses;
    @Autowired    Customers customers;
    @Autowired    ClientListViewBuilder clientListViewBuilder;

    public JComponent build() {

        FleetTableModel tableModel = new FleetTableModel();
        AvailabilityQueryEditor availabilityQueryEditor = new AvailabilityQueryEditor(rentalClasses);
        refresh(availabilityQueryEditor::getQuery, tableModel::setData);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return borderLayout()
                .north(
                        flowLayout(
                                button("Search...", e -> {
                                            JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), availabilityQueryEditor.getComponent(), "Search for available cars...", JOptionPane.PLAIN_MESSAGE);
                                            refresh(availabilityQueryEditor::getQuery, tableModel::setData);
                                        }
                                ),
                                button("Refresh", () -> refresh(availabilityQueryEditor::getQuery, tableModel::setData)),
                                button("Rent selected...", () -> rentClicked(table, tableModel, availabilityQueryEditor::getQuery))
                        ))
                .center(inScrollPane(table))
                .build();
    }

    private void rentClicked(JTable table, FleetTableModel tableModel, Supplier<AvailabilityService.RentQuery> queryProvider) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(table, "Select a car to rent in the table");
        } else {
            BackgroundOperation.execute(
                    clientService::fetchAll,
                    clients -> showRentDialog(table, tableModel.getCarAt(table.convertRowIndexToModel(selectedRow)), queryProvider, tableModel::setData)
            );
        }
    }

    private void showRentDialog(JComponent parent, Car carToRent, Supplier<AvailabilityService.RentQuery> queryProvider, Consumer<Collection<Car>> dataReceiver) {

        ClientListView view = clientListViewBuilder.build();
        JTable clientsTable = view.getTable();
        int option = JOptionPane.showConfirmDialog(
                parent,
                withTitledBorder(view.getComponent(), "Select client to rent to"),
                "Rent " + carToRent,
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Client client = customers.getAt(clientsTable.convertRowIndexToModel(selectedRow));
                BackgroundOperation.execute(
                        () -> rentalService.rent(carToRent, client, queryProvider.get().getAvailableUntil()),
                        () -> refresh(queryProvider, dataReceiver)
                );
            }
        }
    }

    private void refresh(Supplier<AvailabilityService.RentQuery> queryProvider, Consumer<Collection<Car>> dataReceiver) {
        BackgroundOperation.execute(
                () -> availabilityService.findAvailableToRent(queryProvider.get()),
                dataReceiver
        );
    }

    static class AvailabilityQueryEditor {

        UtilDateModel until = new UtilDateModel(Date.from((ZonedDateTime.now().plusDays(7).toInstant())));
        JComboBox<RentalClass> classChooser;
        private JComponent component;

        public AvailabilityQueryEditor(RentalClasses rentalClasses) {
            classChooser = rentalClassChooser(rentalClasses);
            component = simpleForm()
                    .addRow("Rent until:", datePicker(until))
                    .addRow("Class:", classChooser)
                    .build();
        }

        AvailabilityService.RentQuery getQuery() {
            RentalClass selectedItem = (RentalClass) classChooser.getSelectedItem();
            return new AvailabilityService.RentQuery(selectedItem, toMidnight(ZonedDateTime.ofInstant(until.getValue().toInstant(), ZoneId.systemDefault())));
        }

        JComponent getComponent() {
            return component;
        }
    }

}
