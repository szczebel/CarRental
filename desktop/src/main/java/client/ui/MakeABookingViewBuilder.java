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
import java.util.Collection;
import java.util.function.Consumer;
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
        refresh(availabilityQueryEditor::getQuery, tableModel::setData);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return borderLayout()
                .north(
                        toolbar(
                                button("Search...", e -> {
                                            JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), availabilityQueryEditor.createComponent(), "Search for available cars...", JOptionPane.PLAIN_MESSAGE);
                                            refresh(availabilityQueryEditor::getQuery, tableModel::setData);
                                        }
                                ),
                                button("Refresh", () ->          refresh(availabilityQueryEditor::getQuery, tableModel::setData)),
                                button("Book selected...", () -> bookClicked(table, tableModel, availabilityQueryEditor::getQuery))
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
                        () -> refresh(queryProvider, tableModel::setData)
                );
            }
        }
    }

    private void refresh(Supplier<AvailabilityService.BookingQuery> queryProvider, Consumer<Collection<Car>> resultConsumer) {
        BackgroundOperation.execute(
                () -> availabilityService.findAvailableToBook(queryProvider.get()),
                resultConsumer
        );
    }

    static class AvailabilityQueryEditor extends IntervalEditor {

        JComboBox<RentalClass> classChooser;

        public AvailabilityQueryEditor(RentalClasses rentalClasses) {
            super(new Interval(ZonedDateTime.now(), ZonedDateTime.now().plusDays(7)));
            classChooser = rentalClassChooser(rentalClasses);
        }

        @Override
        public JComponent createComponent() {
            return simpleForm()
                    .addRow("From:", datePicker(from))
                    .addRow("To:",   datePicker(to))
                    .addRow("Class:", classChooser)
                    .build();
        }

        AvailabilityService.BookingQuery getQuery() {
            RentalClass selectedItem = (RentalClass) classChooser.getSelectedItem();
            return new AvailabilityService.BookingQuery(selectedItem, getInterval());
        }
    }
}
