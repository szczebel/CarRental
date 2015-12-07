package client.ui.booking;

import client.ui.util.BackgroundOperation;
import client.ui.util.CarResource;
import client.ui.util.CarResourceRenderer;
import client.ui.util.FleetCache;
import common.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import schedule.interaction.InstantTooltips;
import schedule.view.ScheduleView;
import schedule.view.TaskRenderer;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static client.ui.util.GuiHelper.*;

@org.springframework.stereotype.Component
public class BookingsViewBuilder {

    @Autowired    BookingService bookingService;
    @Autowired    FleetCache fleetCache;

    public JComponent build() {
        BookingsModel model = new BookingsModel(fleetCache);
        ScheduleView<CarResource, BookingsModel.BookingAsTask> chart = createChart(model);
        BackgroundOperation.execute(bookingService::getBookings, model.asConsumer());

        return borderLayout()
                .north(
                        toolbar(
                                button("Refresh", () -> BackgroundOperation.execute(bookingService::getBookings, model.asConsumer()))
                        )
                )
                .center(
                        tabbedPane(SwingUtilities.BOTTOM)
                                .addTab("Table", inScrollPane(createTable(model)))
                                .addTab("Chart", chart.getComponent())
                                .build()
                )
                .build();
    }

    private JTable createTable(BookingsModel model) {
        JTable table = new JTable(model);
        table.setDefaultRenderer(ZonedDateTime.class, convertingRenderer(value -> ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm"))));
        return table;
    }

    private ScheduleView<CarResource, BookingsModel.BookingAsTask> createChart(BookingsModel bookingsModel) {
        ScheduleView<CarResource, BookingsModel.BookingAsTask> chart = new ScheduleView<>(bookingsModel);
        chart.setTaskRenderer(new BookingRenderer());
        chart.setResourceRenderer(new CarResourceRenderer());
        chart.setMouseInteractions(InstantTooltips.renderWith(new BookingTooltipRenderer()));
        return chart;
    }

    private static class BookingRenderer extends TaskRenderer.Default<BookingsModel.BookingAsTask> {
        public BookingRenderer() {
            setBackground(Color.pink);
            setOpaque(true);
        }

        @Override
        protected String getTextFromTask(BookingsModel.BookingAsTask event) {
            return event.booking.getClientName();
        }
    }

    private static class BookingTooltipRenderer extends TaskRenderer.Default<BookingsModel.BookingAsTask> {
        @Override
        protected String getTextFromTask(BookingsModel.BookingAsTask event) {
            return "<html>" + event.booking.getClientName() +
                    "<p>" + event.booking.getClientEmail() +
                    "<p>" + event.booking.getModel();
        }
    }
}
