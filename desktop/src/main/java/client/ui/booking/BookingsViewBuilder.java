package client.ui.booking;

import client.ui.util.BackgroundOperation;
import common.domain.Booking;
import common.service.BookingService;
import common.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import swingutils.components.table.TablePanel;

import javax.swing.*;
import java.util.function.Consumer;

import static swingutils.components.ComponentFactory.button;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;

@org.springframework.stereotype.Component
public class BookingsViewBuilder {

    @Autowired
    RentalService rentalService;
    @Autowired
    BookingService bookingService;

    public JComponent build() {
        return new View(rentalService, bookingService).component;
    }

    private static class View {

        private final RentalService rentalService;
        private final BookingService bookingService;
        private final Bookings model;
        final JComponent component;

        View(RentalService rentalService, BookingService bookingService) {
            this.rentalService = rentalService;
            this.bookingService = bookingService;
            model = new Bookings();
            reloadBookings();
            TablePanel<Booking> table = model.createTable();

            component = borderLayout()
                    .north(
                            flowLayout(
                                    button("Rent selected", () -> ifBookingSelected(table.getSelection(), this::rent)),
                                    button("Cancel selected", () -> ifBookingSelected(table.getSelection(), this::cancel)),
                                    table.getToolbar()
                            )
                    )
                    .center(
                            table.getScrollPane()
                    )
                    .build();
        }

        private void ifBookingSelected(Booking selection, Consumer<Booking> action) {
            if (selection == null) return;//todo tell user to select sth
            action.accept(selection);
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
