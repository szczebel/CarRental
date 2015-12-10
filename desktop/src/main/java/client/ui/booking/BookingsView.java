package client.ui.booking;

import client.ui.Customers;
import client.ui.util.BackgroundOperation;
import client.ui.util.FleetCache;
import common.domain.Booking;
import common.domain.Client;
import common.service.BookingService;
import common.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static client.ui.util.GuiHelper.*;

@org.springframework.stereotype.Component
public class BookingsView {

    @Autowired
    RentalService rentalService;
    @Autowired
    BookingService bookingService;
    @Autowired
    FleetCache fleetCache;
    @Autowired
    Customers customers;


    private BookingsModel model;
    private JComboBox<Client> customerChooser;

    public JComponent build() {
        model = new BookingsModel(fleetCache);
        customerChooser = new JComboBox<>(customers.getComboBoxModel()); //todo: type ahead
        customerChooser.setRenderer(convertingListCellRenderer(value -> value != null ? value.getName() : "<all>"));
        JTable table = createTable(model);
        reloadBookings();
        customerChooser.addActionListener(e -> reloadBookings());

        return borderLayout()
                .north(
                        toolbar(
                                customerChooser,
                                button("Rent selected", () -> ifBookingSelected(table, this::rent)),
                                button("Cancel selected", () -> ifBookingSelected(table, this::cancel))
                        )
                )
                .center(
                        tabbedPane(SwingUtilities.BOTTOM)
                                .addTab("Table", inScrollPane(table))
                                .build()
                )
                .build();
    }

    private void ifBookingSelected(JTable table, Consumer<Booking> action) {
        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1) return;//todo tell user to select sth
        Booking booking = model.getBooking(table.convertRowIndexToModel(selectedRow));
        action.accept(booking);
    }

    private void rent(Booking b) {
        BackgroundOperation.execute(() -> rentalService.rent(b), this::reloadBookings);

    }
    private void cancel(Booking b) {
        BackgroundOperation.execute(() -> bookingService.cancel(b), this::reloadBookings);
    }

    private void reloadBookings() {
        Client client = (Client) customerChooser.getSelectedItem();
        BackgroundOperation.execute(()-> bookingService.getBookingsOf(client), model::setData);
    }

    private JTable createTable(BookingsModel model) {
        JTable table = new JTable(model);
        table.setDefaultRenderer(ZonedDateTime.class, convertingRenderer(value -> ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm"))));
        return table;
    }

}
