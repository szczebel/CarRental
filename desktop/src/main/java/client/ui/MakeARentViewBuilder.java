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
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static client.ui.util.GuiHelper.*;
import static common.util.TimeUtils.toMidnight;

@Component
public class MakeARentViewBuilder {

    @Autowired    AvailabilityService availabilityService;
    @Autowired    RentalService rentalService;
    @Autowired    ClientService clientService;
    @Autowired    RentalClasses rentalClasses;
    @Autowired    Customers customers;

    public JComponent build() {

        FleetTableModel tableModel = new FleetTableModel();
        AvailabilityQueryEditor availabilityQueryEditor = new AvailabilityQueryEditor(rentalClasses);
        refresh(tableModel, availabilityQueryEditor);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return borderLayout()
                .north(
                        toolbar(
                                button("Search...", e -> {
                                            JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), availabilityQueryEditor.getComponent(), "Search for available cars...", JOptionPane.PLAIN_MESSAGE);
                                            refresh(tableModel, availabilityQueryEditor);
                                        }
                                ),
                                button("Refresh", () -> refresh(tableModel, availabilityQueryEditor)),
                                button("Rent selected...", () -> rentClicked(table, tableModel, availabilityQueryEditor))
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
                    clients -> showRentDialog(table, clients, tableModel.getCarAt(table.convertRowIndexToModel(selectedRow)), tableModel, queryProvider)
            );
        }
    }

    private void showRentDialog(JComponent parent, List<Client> clients, Car carToRent, FleetTableModel tableModel, Supplier<AvailabilityService.RentQuery> queryProvider) {

        JTable clientsTable = new JTable(customers);
        int option = JOptionPane.showConfirmDialog(
                parent,
                withTitledBorder(inScrollPane(clientsTable), "Select client to rent to"),
                "Rent " + carToRent,
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Client client = customers.getAt(clientsTable.convertRowIndexToModel(selectedRow));
                BackgroundOperation.execute(
                        () -> rentalService.rent(carToRent, client, queryProvider.get().getAvailableUntil()),
                        () -> refresh(tableModel, queryProvider)
                );
            }
        }
    }

    private void refresh(FleetTableModel tableModel, Supplier<AvailabilityService.RentQuery> queryProvider) {
        BackgroundOperation.execute(
                () -> availabilityService.findAvailableToRent(queryProvider.get()),
                tableModel::setData
        );
    }

    static class AvailabilityQueryEditor implements Supplier<AvailabilityService.RentQuery>{

        UtilDateModel until = new UtilDateModel(Date.from((ZonedDateTime.now().plusDays(7).toInstant())));
        JComboBox<RentalClass> classChooser;
        private JComponent component;

        public AvailabilityQueryEditor(RentalClasses rentalClasses) {
            classChooser = rentalClassChooser(rentalClasses);
            component = borderLayout()
                    .north(classChooser)
                    .south(datePicker(until))
                    .build();
        }

        AvailabilityService.RentQuery getQuery() {
            RentalClass selectedItem = (RentalClass) classChooser.getSelectedItem();
            return new AvailabilityService.RentQuery(selectedItem, toMidnight(ZonedDateTime.ofInstant(until.getValue().toInstant(), ZoneId.systemDefault())));
        }

        JComponent getComponent() {
            return component;
        }


        @Override
        public AvailabilityService.RentQuery get() {
            return getQuery();
        }
    }

}
