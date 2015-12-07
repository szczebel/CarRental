package client.ui.fullscheduleview;

import client.ui.util.BackgroundOperation;
import client.ui.util.CarResource;
import client.ui.util.CarResourceRenderer;
import client.ui.util.FleetCache;
import common.service.BookingService;
import common.service.HistoryService;
import common.service.RentalService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import schedule.interaction.InstantTooltips;
import schedule.view.ScheduleView;

import javax.swing.*;
import java.time.ZonedDateTime;

import static client.ui.util.GuiHelper.*;

@org.springframework.stereotype.Component
public class FullScheduleViewBuilder {

    @Autowired
    BookingService bookingService;
    @Autowired
    RentalService rentalService;
    @Autowired
    HistoryService historyService;
    @Autowired
    FleetCache fleetCache;

    public JComponent build() {
        FullScheduleModel model = new FullScheduleModel(fleetCache);
        ScheduleView<CarResource, AbstractAssignmentAsTask> chart = createChart(model);

        return borderLayout()
                .north(
                        toolbar(
                                button("Refresh", () -> refresh(model))
                        )
                )
                .center(
                        chart.getComponent()
                )
                .build();
    }

    private void refresh(FullScheduleModel fullScheduleModel) {
        fullScheduleModel.clear();
        HistoryService.Query query = new HistoryService.Query(new Interval(ZonedDateTime.now().minusDays(30), ZonedDateTime.now()));
        BackgroundOperation.execute(() -> historyService.fetchHistory(query), r -> fullScheduleModel.addHistory(r.getRecords()));
        BackgroundOperation.execute(rentalService::getCurrentRentals, fullScheduleModel::addCurrent);
        BackgroundOperation.execute(bookingService::getBookings, fullScheduleModel::addBookings);
    }

    private ScheduleView<CarResource, AbstractAssignmentAsTask> createChart(FullScheduleModel fullScheduleModel) {
        ScheduleView<CarResource, AbstractAssignmentAsTask> chart = new ScheduleView<>(fullScheduleModel);
        chart.setTaskRenderer(new AbstractAssignmentRenderer<>());
        chart.setResourceRenderer(new CarResourceRenderer());
        chart.setMouseInteractions(InstantTooltips.renderWith(new TooltipRenderer<>()));
        return chart;
    }
}
