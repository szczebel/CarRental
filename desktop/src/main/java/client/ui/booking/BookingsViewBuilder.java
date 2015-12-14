package client.ui.booking;

import client.ui.FleetCache;
import client.ui.util.BackgroundOperation;
import client.ui.util.FilterableTable;
import common.domain.Booking;
import common.service.BookingService;
import common.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static client.ui.util.GuiHelper.convertingRenderer;
import static swingutils.components.ComponentFactory.*;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;

@org.springframework.stereotype.Component
public class BookingsViewBuilder {

    @Autowired
    RentalService rentalService;
    @Autowired
    BookingService bookingService;
    @Autowired
    FleetCache fleetCache;

    public JComponent build() {
        return new View(rentalService, bookingService, fleetCache).component;
    }

    static class View {

        private final RentalService rentalService;
        private final BookingService bookingService;
        private final BookingsModel model;
        final JComponent component;

        View(RentalService rentalService, BookingService bookingService, FleetCache fleetCache) {
            this.rentalService = rentalService;
            this.bookingService = bookingService;
            model = new BookingsModel(fleetCache);
            FilterableTable ft = FilterableTable.create(model);
            ft.table.setDefaultRenderer(ZonedDateTime.class, convertingRenderer(value -> ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm"))));
            reloadBookings();

            component = borderLayout()
                    .north(
                            flowLayout(
                                    label("Filter"),
                                    ft.filter,
                                    button("Rent selected", () -> ifBookingSelected(ft.table, this::rent)),
                                    button("Cancel selected", () -> ifBookingSelected(ft.table, this::cancel))
                            )
                    )
                    .center(
                            inScrollPane(ft.table)
                    )
                    .build();
        }

        private void ifBookingSelected(JTable table, Consumer<Booking> action) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) return;//todo tell user to select sth
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
            BackgroundOperation.execute(bookingService::getBookings, model::setData);
        }
    }
}
