package client.ui.history;

import client.ui.fullscheduleview.AbstractAssignmentRenderer;
import client.ui.fullscheduleview.TooltipRenderer;
import client.ui.interval.IntervalEditor;
import client.ui.util.BackgroundOperation;
import client.ui.util.CarResource;
import client.ui.util.CarResourceRenderer;
import client.ui.util.FleetCache;
import common.domain.RentalHistory;
import common.service.HistoryService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import schedule.interaction.InstantTooltips;
import schedule.view.ScheduleView;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static client.ui.util.GuiHelper.*;

@org.springframework.stereotype.Component
public class HistoricalRentalsViewBuilder {

    @Autowired    HistoryService historyService;
    @Autowired    FleetCache fleetCache;

    public JComponent build() {
        HistoricalRentalsModel model = new HistoricalRentalsModel(fleetCache);
        ScheduleView<CarResource, HistoricalRentalAsTask> chart = createChart(model);
        RentalHistoryStatisticsView statisticsView = new RentalHistoryStatisticsView();
        IntervalEditor intervalEditor = new IntervalEditor(new Interval(ZonedDateTime.now().minusDays(30), ZonedDateTime.now()));

        refresh(intervalEditor::getInterval, model::setData, statisticsView::setData);

        return borderLayout()
                .north(buildToolbar(
                        intervalEditor,
                        model::setData,
                        statisticsView::setData
                        ))
                .center(
                        tabbedPane(SwingUtilities.BOTTOM)
                                .addTab("Table", inScrollPane(createTable(model)))
                                .addTab("Chart", chart.getComponent())
                                .addTab("Statistics", statisticsView.getComponent())
                                .build()
                )
                .build();
    }

    private JTable createTable(HistoricalRentalsModel model) {
        JTable table = new JTable(model);
        table.setDefaultRenderer(ZonedDateTime.class, convertingRenderer(value -> ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm"))));
        table.setDefaultRenderer(Duration.class, convertingRenderer(value -> ((Duration) value).toHours() + " hours"));
        return table;
    }

    private ScheduleView<CarResource, HistoricalRentalAsTask> createChart(HistoricalRentalsModel tableModel) {
        ScheduleView<CarResource, HistoricalRentalAsTask> chart = new ScheduleView<>(tableModel);
        chart.setTaskRenderer(new AbstractAssignmentRenderer<>());
        chart.setResourceRenderer(new CarResourceRenderer());
        chart.setMouseInteractions(InstantTooltips.renderWith(new TooltipRenderer<>()));
        return chart;
    }

    private JComponent buildToolbar(IntervalEditor intervalEditor, Consumer<RentalHistory> model, Consumer<RentalHistory.Statistics> statisticsConsumer) {

        return toolbar(
                button("Change criteria", e -> {
                            JOptionPane.showMessageDialog((Component) e.getSource(), intervalEditor.createComponent(), "Change search criteria", JOptionPane.PLAIN_MESSAGE);
                            refresh(intervalEditor::getInterval, model, statisticsConsumer);
                        }
                ),
                button("Refresh", () -> refresh(intervalEditor::getInterval, model, statisticsConsumer))
        );
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
