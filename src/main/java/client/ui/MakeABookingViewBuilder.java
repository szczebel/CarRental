package client.ui;

import client.ui.interval.IntervalEditor;
import client.ui.util.BackgroundOperation;
import common.domain.Car;
import common.domain.Client;
import common.domain.RentalClass;
import common.service.BookabilityService;
import common.service.BookingService;
import common.service.ClientService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Supplier;

import static client.ui.util.GuiHelper.*;

@Component
public class MakeABookingViewBuilder {

    @Autowired    BookabilityService bookabilityService;
    @Autowired    BookingService bookingService;
    @Autowired    ClientService clientService;
    @Autowired    RentalClasses rentalClasses;

    //todo add schedule chart to visualize bookability?
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

    private void bookClicked(JTable table, FleetTableModel tableModel, Supplier<BookabilityService.Query> classChooser) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(table, "Select a car to book in the table");
        } else {
            BackgroundOperation.execute(
                    clientService::fetchAll,
                    clients -> showBookDialog(table, clients, tableModel.getCarAt(table.convertRowIndexToModel(selectedRow)), tableModel, classChooser)
            );
        }
    }

    private void showBookDialog(JComponent parent, List<Client> clients, Car carToBook, FleetTableModel tableModel, Supplier<BookabilityService.Query> queryProvider) {

        ClientListTableModel clientsModel = new ClientListTableModel();
        clientsModel.setData(clients);
        JTable clientsTable = new JTable(clientsModel);
        int option = JOptionPane.showConfirmDialog(
                parent,
                withTitledBorder(inScrollPane(clientsTable), "Select client to book for"),
                "Rent " + carToBook,
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Client client = clientsModel.getClientAt(clientsTable.convertRowIndexToModel(selectedRow));
                BackgroundOperation.execute(
                        () -> bookingService.book(carToBook, client, queryProvider.get().getInterval()),
                        () -> refresh(tableModel, queryProvider)
                );
            }
        }
    }

    private void refresh(FleetTableModel tableModel, Supplier<BookabilityService.Query> queryProvider) {
        BackgroundOperation.execute(
                () -> bookabilityService.findAvailableCars(queryProvider.get()),
                tableModel::setData
        );
    }

    static class AvailabilityQueryEditor {

        IntervalEditor intervalEditor = new IntervalEditor(new Interval(ZonedDateTime.now(), ZonedDateTime.now().plusDays(7)));
        JComboBox<RentalClass> classChooser;
        private JComponent component;

        public AvailabilityQueryEditor(RentalClasses rentalClasses) {
            classChooser = new JComboBox<>(rentalClasses.getComboBoxModel());
            component =
                    borderLayout()
                            .center(label("Rental class : "))
                            .east(classChooser)
                            .south(intervalEditor.getComponent())
                            .build();
        }

        BookabilityService.Query getQuery() {
            RentalClass selectedItem = (RentalClass) classChooser.getSelectedItem();
            return new BookabilityService.Query(selectedItem, intervalEditor.getInterval());
        }

        JComponent getComponent() {
            return component;
        }

        Supplier<BookabilityService.Query> asSupplier() {
            return this::getQuery;
        }
    }
}
