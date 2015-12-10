package client.ui;

import client.ui.interval.IntervalEditor;
import client.ui.util.BackgroundOperation;
import common.domain.Car;
import common.domain.Client;
import common.domain.RentalClass;
import common.service.AvailabilityService;
import common.service.BookingService;
import common.service.ClientService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

import static client.ui.util.GuiHelper.*;

@Component
public class MakeABookingViewBuilder {

    @Autowired    AvailabilityService availabilityService;
    @Autowired    BookingService bookingService;
    @Autowired    ClientService clientService;
    @Autowired    RentalClasses rentalClasses;
    @Autowired    Customers customers;

    public JComponent build() {
        FleetTableModel tableModel = new FleetTableModel();
        AvailabilityQueryEditor availabilityQueryEditor = new AvailabilityQueryEditor(rentalClasses);
        refresh(tableModel, availabilityQueryEditor.asSupplier());
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return borderLayout()
                .north(
                        toolbar(
                                button("Search...", e -> {
                                            JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), availabilityQueryEditor.getComponent(), "Search for available cars...", JOptionPane.PLAIN_MESSAGE);
                                            refresh(tableModel, availabilityQueryEditor.asSupplier());
                                        }
                                ),
                                button("Refresh", () -> refresh(tableModel, availabilityQueryEditor.asSupplier())),
                                button("Book selected...", () -> bookClicked(table, tableModel, availabilityQueryEditor.asSupplier()))
                        ))
                .center(inScrollPane(table))
                .build();
    }

    private void bookClicked(JTable table, FleetTableModel tableModel, Supplier<AvailabilityService.BookingQuery> classChooser) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(table, "Select a car to book in the table");
        } else {
            BackgroundOperation.execute(
                    clientService::fetchAll,
                    clients -> showBookDialog(table, tableModel.getCarAt(table.convertRowIndexToModel(selectedRow)), tableModel, classChooser)
            );
        }
    }

    private void showBookDialog(JComponent parent, Car carToBook, FleetTableModel tableModel, Supplier<AvailabilityService.BookingQuery> queryProvider) {

        JTable clientsTable = new JTable(customers);
        int option = JOptionPane.showConfirmDialog(
                parent,
                withTitledBorder(inScrollPane(clientsTable), "Select client to book for"),
                "Rent " + carToBook,
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Client client = customers.getAt(clientsTable.convertRowIndexToModel(selectedRow));
                BackgroundOperation.execute(
                        () -> bookingService.book(carToBook, client, queryProvider.get().getInterval()),
                        () -> refresh(tableModel, queryProvider)
                );
            }
        }
    }

    private void refresh(FleetTableModel tableModel, Supplier<AvailabilityService.BookingQuery> queryProvider) {
        BackgroundOperation.execute(
                () -> availabilityService.findAvailableToBook(queryProvider.get()),
                tableModel::setData
        );
    }

    static class AvailabilityQueryEditor {

        IntervalEditor intervalEditor = new IntervalEditor(new Interval(ZonedDateTime.now(), ZonedDateTime.now().plusDays(7)));
        JComboBox<RentalClass> classChooser;
        private JComponent component;

        public AvailabilityQueryEditor(RentalClasses rentalClasses) {
            classChooser = rentalClassChooser(rentalClasses);
            component =
                    borderLayout()
                            .center(label("Rental class : "))
                            .east(classChooser)
                            .south(intervalEditor.getComponent())
                            .build();
        }

        AvailabilityService.BookingQuery getQuery() {
            RentalClass selectedItem = (RentalClass) classChooser.getSelectedItem();
            return new AvailabilityService.BookingQuery(selectedItem, intervalEditor.getInterval());
        }

        JComponent getComponent() {
            return component;
        }

        Supplier<AvailabilityService.BookingQuery> asSupplier() {
            return this::getQuery;
        }
    }
}
