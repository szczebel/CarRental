package client.ui.history;

import client.ui.FleetCache;
import client.ui.scheduleview.AbstractAssignmentRenderer;
import client.ui.scheduleview.CarResource;
import client.ui.scheduleview.CarResourceRenderer;
import client.ui.scheduleview.TooltipRenderer;
import client.ui.util.BackgroundOperation;
import client.ui.util.IntervalEditor;
import common.domain.HistoricalRental;
import common.domain.RentalHistory;
import common.service.HistoryService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import schedule.interaction.InstantTooltips;
import schedule.view.ScheduleView;
import swingutils.components.table.TablePanel;

import javax.swing.*;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static swingutils.components.ComponentFactory.button;
import static swingutils.layout.LayoutBuilders.*;

@org.springframework.stereotype.Component
public class HistoricalRentalsViewBuilder {

    @Autowired
    HistoryService historyService;
    @Autowired
    FleetCache fleetCache;

    public JComponent build() {
        HistoricalRentalsModel model = new HistoricalRentalsModel(fleetCache);
        ScheduleView<CarResource, HistoricalRentalAsTask> chart = createChart(model);
        RentalHistoryStatisticsView statisticsView = new RentalHistoryStatisticsView();
        IntervalEditor intervalEditor = new IntervalEditor(new Interval(ZonedDateTime.now().minusDays(30), ZonedDateTime.now()));
        TablePanel<HistoricalRental> table = model.createTable();

        refresh(intervalEditor::getInterval, model::setData, statisticsView::setData);

        return borderLayout()
                .north(
                        flowLayout(
                                button("Change criteria", () -> {
                                            changeCriteria(model, statisticsView, intervalEditor);
                                        }
                                ),
                                button("Refresh", () -> refresh(intervalEditor::getInterval, model::setData, statisticsView::setData))
                        ))
                .center(
                        tabbedPane(SwingUtilities.RIGHT)
                                .addTab("Table", table.getComponent())
                                .addTab("Chart", chart.getComponent())
                                .addTab("Statistics", statisticsView.getComponent())
                                .build()
                )
                .build();
    }

    private void changeCriteria(HistoricalRentalsModel model, RentalHistoryStatisticsView statisticsView, IntervalEditor intervalEditor) {
        JOptionPane.showMessageDialog(null, intervalEditor.createComponent(), "Change search criteria", JOptionPane.PLAIN_MESSAGE);
        refresh(intervalEditor::getInterval, model::setData, statisticsView::setData);
    }

    private ScheduleView<CarResource, HistoricalRentalAsTask> createChart(HistoricalRentalsModel model) {
        ScheduleView<CarResource, HistoricalRentalAsTask> chart = new ScheduleView<>(model.asScheduleModel());
        chart.setTaskRenderer(new AbstractAssignmentRenderer<>());
        chart.setResourceRenderer(new CarResourceRenderer());
        chart.setMouseInteractions(InstantTooltips.renderWith(new TooltipRenderer<>()));
        return chart;
    }

    private void refresh(Supplier<Interval> intervalSupplier, Consumer<RentalHistory> model, Consumer<RentalHistory.Statistics> statisticsConsumer) {
        BackgroundOperation.execute(
                () -> historyService.fetchHistory(new HistoryService.Query(intervalSupplier.get())),
                result -> {
                    model.accept(result);
                    statisticsConsumer.accept(result.getStatistics());
                }
        );
    }

}
