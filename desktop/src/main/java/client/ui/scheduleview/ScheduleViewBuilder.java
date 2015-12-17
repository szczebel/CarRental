package client.ui.scheduleview;

import client.ui.FleetCache;
import common.domain.AbstractAssignment;
import common.service.BookingService;
import common.service.HistoryService;
import common.service.RentalService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import schedule.interaction.InstantTooltips;
import schedule.view.ScheduleView;
import swingutils.BackgroundOperation;

import javax.swing.*;
import java.time.ZonedDateTime;

import static client.ui.util.GuiHelper.textField;
import static swingutils.components.ComponentFactory.button;
import static swingutils.components.ComponentFactory.label;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;

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
                        flowLayout(
                                button("Refresh", () -> refresh(model)),
                                label("Filter assignments:"),
                                textField(10, s -> model.setTaskFilter(t -> containsString(t, s)))
                        )
                )
                .center(
                        chart.getComponent()
                )
                .build();
    }

    private boolean containsString(AbstractAssignmentAsTask t, String s) {
        AbstractAssignment aa = t.getAbstractAssignment();
        return aa.getClientEmail().contains(s) ||
                aa.getClientName().contains(s) ||
                aa.getStart().getDayOfWeek().name().contains(s);
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
