package client.ui.booking;

import client.ui.fullscheduleview.AbstractAssignmentRenderer;
import client.ui.fullscheduleview.TooltipRenderer;
import client.ui.util.BackgroundOperation;
import client.ui.util.CarResource;
import client.ui.util.CarResourceRenderer;
import client.ui.util.FleetCache;
import common.domain.Booking;
import common.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import schedule.interaction.InstantTooltips;
import schedule.view.ScheduleView;

import javax.swing.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.function.Consumer;

import static client.ui.util.GuiHelper.*;

@org.springframework.stereotype.Component
public class BookingsViewBuilder {

    @Autowired
    BookingService bookingService;
    @Autowired
    FleetCache fleetCache;

    public JComponent build() {
        BookingsModel model = new BookingsModel(fleetCache);
        ScheduleView<CarResource, BookingAsTask> chart = createChart(model);
        reloadBookings(model.asConsumer());
        JTable table = createTable(model);

        return borderLayout()
                .north(
                        toolbar(
                                button("Refresh", () -> reloadBookings(model.asConsumer())),
                                button("Cancel", () -> cancelSelectedBooking(table, model))
                        )
                )
                .center(
                        tabbedPane(SwingUtilities.BOTTOM)
                                .addTab("Table", inScrollPane(table))
                                .addTab("Chart", chart.getComponent())
                                .build()
                )
                .build();
    }

    private void cancelSelectedBooking(JTable table, BookingsModel model) {
        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1) return;//todo tell user to select sth
        Booking booking = model.getBooking(table.convertRowIndexToModel(selectedRow));
        //todo implement this
        //BackgroundOperation.execute(() -> bookingService.cancel(booking), () -> reloadBookings(model.asConsumer()));
    }

    private void reloadBookings(Consumer<Collection<Booking>> successHandler) {
        BackgroundOperation.execute(bookingService::getBookings, successHandler);
    }

    private JTable createTable(BookingsModel model) {
        JTable table = new JTable(model);
        table.setDefaultRenderer(ZonedDateTime.class, convertingRenderer(value -> ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm"))));
        return table;
    }

    private ScheduleView<CarResource, BookingAsTask> createChart(BookingsModel bookingsModel) {
        ScheduleView<CarResource, BookingAsTask> chart = new ScheduleView<>(bookingsModel);
        chart.setTaskRenderer(new AbstractAssignmentRenderer<>());
        chart.setResourceRenderer(new CarResourceRenderer());
        chart.setMouseInteractions(InstantTooltips.renderWith(new TooltipRenderer<>()));

        return chart;
    }

}
