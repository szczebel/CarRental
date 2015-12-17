package client.ui.history;

import client.ui.FleetCache;
import client.ui.scheduleview.*;
import client.ui.util.IntervalEditor;
import common.domain.AbstractAssignment;
import common.domain.HistoricalRental;
import common.domain.RentalHistory;
import common.service.HistoryService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import schedule.basic.GenericScheduleModel;
import schedule.interaction.InstantTooltips;
import schedule.model.ScheduleModel;
import schedule.view.ScheduleView;
import swingutils.BackgroundOperation;
import swingutils.components.GradientPanel;
import swingutils.components.progress.BusyFactory;
import swingutils.components.progress.ProgressIndicatingComponent;
import swingutils.components.progress.ProgressIndicator;
import swingutils.components.table.TablePanel;
import swingutils.layout.cards.CardSwitcherFactory;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static client.ui.util.GuiHelper.textField;
import static swingutils.components.ComponentFactory.button;
import static swingutils.components.ComponentFactory.label;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;
import static swingutils.layout.cards.CardSwitcherFactory.cardLayout;

@org.springframework.stereotype.Component
public class HistoricalRentalsViewBuilder {

    @Autowired
    HistoryService historyService;
    @Autowired
    FleetCache fleetCache;

    public JComponent build() {
        HistoricalRentalsModel model = new HistoricalRentalsModel(fleetCache);
        GenericScheduleModel<CarResource, HistoricalRentalAsTask> scheduleModel = model.asScheduleModel();
        ScheduleView<CarResource, HistoricalRentalAsTask> chart = createChart(scheduleModel);
        RentalHistoryStatisticsView statisticsView = new RentalHistoryStatisticsView();
        IntervalEditor intervalEditor = new IntervalEditor(new Interval(ZonedDateTime.now().minusDays(30), ZonedDateTime.now()));
        TablePanel<HistoricalRental> table = model.createTable();

        ProgressIndicatingComponent pi = BusyFactory.lockAndWhirlWhenBusy();

        pi.setContent(borderLayout()
                .north(
                        flowLayout(
                                button("Change criteria", () -> {
                                            changeCriteria(model, statisticsView, intervalEditor, pi);
                                        }
                                ),
                                button("Refresh", () -> refresh(intervalEditor::getInterval, model::setData, statisticsView::setData, pi))
                        ))
                .center(
                        cardLayout(CardSwitcherFactory.MenuPlacement.TOP, new GradientPanel(Color.white, Color.lightGray, false))
                                .addTab("Table", table.getComponent())
                                .addTab("Chart",
                                        borderLayout()
                                                .north(flowLayout(
                                                        label("Filter assignments:"),
                                                        textField(10, s -> scheduleModel.setTaskFilter(t -> containsString(t, s)))
                                                ))
                                                .center(chart.getComponent())
                                                .build()
                                )
                                .addTab("Statistics", statisticsView.getComponent())
                                .build()
                )
                .build());
        refresh(intervalEditor::getInterval, model::setData, statisticsView::setData, pi);

        return pi.getComponent();
    }

    private void changeCriteria(HistoricalRentalsModel model, RentalHistoryStatisticsView statisticsView, IntervalEditor intervalEditor, ProgressIndicator pi) {
        JOptionPane.showMessageDialog(null, intervalEditor.createComponent(), "Change search criteria", JOptionPane.PLAIN_MESSAGE);
        refresh(intervalEditor::getInterval, model::setData, statisticsView::setData, pi);
    }

    private ScheduleView<CarResource, HistoricalRentalAsTask> createChart(ScheduleModel<CarResource, HistoricalRentalAsTask> scheduleModel) {
        ScheduleView<CarResource, HistoricalRentalAsTask> chart = new ScheduleView<>(scheduleModel);
        chart.setTaskRenderer(new AbstractAssignmentRenderer<>());
        chart.setResourceRenderer(new CarResourceRenderer());
        chart.setMouseInteractions(InstantTooltips.renderWith(new TooltipRenderer<>()));
        return chart;
    }

    private boolean containsString(AbstractAssignmentAsTask t, String s) {
        AbstractAssignment aa = t.getAbstractAssignment();
        return aa.getClientEmail().contains(s) ||
                aa.getClientName().contains(s) ||
                aa.getStart().getDayOfWeek().name().contains(s);
    }

    private void refresh(Supplier<Interval> intervalSupplier, Consumer<RentalHistory> model, Consumer<RentalHistory.Statistics> statisticsConsumer, ProgressIndicator pi) {
        BackgroundOperation.execute(
                () -> historyService.fetchHistory(new HistoryService.Query(intervalSupplier.get())),
                result -> {
                    model.accept(result);
                    statisticsConsumer.accept(result.getStatistics());
                },
                pi
        );
    }

}
