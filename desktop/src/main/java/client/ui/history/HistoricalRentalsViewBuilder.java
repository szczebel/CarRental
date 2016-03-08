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
import schedule.interaction.InstantTooltips;
import schedule.model.GenericScheduleModel;
import schedule.model.ScheduleModel;
import schedule.view.ScheduleView;
import swingutils.background.BackgroundOperation;
import swingutils.components.progress.BusyFactory;
import swingutils.components.progress.ProgressIndicatingComponent;
import swingutils.components.progress.ProgressIndicator;
import swingutils.components.table.TablePanel;
import swingutils.layout.cards.CardMenuBuilder;
import swingutils.layout.cards.CardMenuBuilders;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static client.ui.util.GuiHelper.textField;
import static swingutils.components.ComponentFactory.*;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;
import static swingutils.layout.cards.CardLayoutBuilder.cardLayout;
import static swingutils.layout.cards.MenuPlacement.RIGHT;

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

        CardMenuBuilder<JComponent> cardMenuBuilder = CardMenuBuilders.BorderedOrange()
                .menuBarCustomizer(bar -> decorate(bar).withEmptyBorder(0, 4, 4, 4).get())
                .menuPlacement(RIGHT);
        pi.setContent(borderLayout()
                .north(
                        flowLayout(
                                button("Change criteria", () -> changeCriteria(model, statisticsView, intervalEditor, pi)),
                                button("Refresh", () -> refresh(intervalEditor::getInterval, model::setData, statisticsView::setData, pi))
                        ))
                .center(
                        cardLayout(cardMenuBuilder)
                                .addTab("Table", decorate(table.getComponent()).withLineBorder(SystemColor.controlShadow).get())
                                .addTab("Chart", decorate(buildChartComponent(scheduleModel, chart)).withLineBorder(SystemColor.controlShadow).get())
                                .addTab("Stats", decorate(statisticsView.getComponent()).withLineBorder(SystemColor.controlShadow).get())
                                .build()
                )
                .build());
        refresh(intervalEditor::getInterval, model::setData, statisticsView::setData, pi);

        return pi.getComponent();
    }

    private JPanel buildChartComponent(GenericScheduleModel<CarResource, HistoricalRentalAsTask> scheduleModel, ScheduleView<CarResource, HistoricalRentalAsTask> chart) {
        return borderLayout()
                .north(flowLayout(
                        label("Filter assignments:"),
                        textField(10, string -> scheduleModel.setTaskFilter(task -> containsString(task, string)))
                ))
                .center(chart.getComponent())
                .build();
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
