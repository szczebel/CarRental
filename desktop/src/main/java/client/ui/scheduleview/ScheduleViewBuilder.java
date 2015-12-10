package client.ui.scheduleview;

import client.ui.FleetCache;
import client.ui.util.BackgroundOperation;
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
public class ScheduleViewBuilder {

    @Autowired
    BookingService bookingService;
    @Autowired
    RentalService rentalService;
    @Autowired
    HistoryService historyService;
    @Autowired
    FleetCache fleetCache;

    public JComponent build() {
        AssignmentScheduleModel model = new AssignmentScheduleModel(fleetCache);
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

    private void refresh(AssignmentScheduleModel assignmentScheduleModel) {
        assignmentScheduleModel.clear();
        HistoryService.Query query = new HistoryService.Query(new Interval(ZonedDateTime.now().minusDays(30), ZonedDateTime.now()));
        BackgroundOperation.execute(() -> historyService.fetchHistory(query), r -> assignmentScheduleModel.addHistory(r.getRecords()));
        BackgroundOperation.execute(rentalService::getCurrentRentals, assignmentScheduleModel::addCurrent);
        BackgroundOperation.execute(bookingService::getBookings, assignmentScheduleModel::addBookings);
    }

    private ScheduleView<CarResource, AbstractAssignmentAsTask> createChart(AssignmentScheduleModel assignmentScheduleModel) {
        ScheduleView<CarResource, AbstractAssignmentAsTask> chart = new ScheduleView<>(assignmentScheduleModel);
        chart.setTaskRenderer(new AbstractAssignmentRenderer<>());
        chart.setResourceRenderer(new CarResourceRenderer());
        chart.setMouseInteractions(InstantTooltips.renderWith(new TooltipRenderer<>()));//todo: rightclick&selectionlistener to return/rent/book
        return chart;
    }
}
