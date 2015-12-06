package client.ui.history;

import client.ui.interval.IntervalEditor;
import client.ui.util.BackgroundOperation;
import client.ui.util.CarResource;
import client.ui.util.CarResourceRenderer;
import common.domain.RentalHistory;
import common.service.HistoryService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import schedule.interaction.InstantTooltips;
import schedule.view.ScheduleView;
import schedule.view.TaskRenderer;

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

    @Autowired
    HistoryService historyService;

    public JComponent build() {
        HistoricalRentalsModel model = new HistoricalRentalsModel();
        ScheduleView<CarResource, HistoricalRentalsModel.HistoricalRentalAsTask> chart = createChart(model);
        RentalHistoryStatisticsView statisticsView = new RentalHistoryStatisticsView();

        return borderLayout()
                .north(buildToolbar(model.asConsumer(), statisticsView.asConsumer()))
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

    private ScheduleView<CarResource, HistoricalRentalsModel.HistoricalRentalAsTask> createChart(HistoricalRentalsModel tableModel) {
        ScheduleView<CarResource, HistoricalRentalsModel.HistoricalRentalAsTask> chart = new ScheduleView<>(tableModel);
        chart.setTaskRenderer(new HistoricalRentalRenderer());
        chart.setResourceRenderer(new CarResourceRenderer());
        chart.setMouseInteractions(InstantTooltips.renderWith(new HistoricalRentalTooltipRenderer()));
        return chart;
    }

    private JComponent buildToolbar(Consumer<RentalHistory> model, Consumer<RentalHistory.Statistics> statisticsConsumer) {
        IntervalEditor intervalEditor = new IntervalEditor(new Interval(ZonedDateTime.now().minusDays(30), ZonedDateTime.now()));

        return toolbar(
                button("Change criteria", e -> {
                            JOptionPane.showMessageDialog((Component) e.getSource(), intervalEditor.getComponent(), "Change search criteria", JOptionPane.PLAIN_MESSAGE);
                            searchClicked(intervalEditor.asProvider(), model, statisticsConsumer);
                        }
                ),
                button("Refresh", () -> searchClicked(intervalEditor.asProvider(), model, statisticsConsumer))
        );
    }

    private void searchClicked(Supplier<Interval> intervalSupplier, Consumer<RentalHistory> model, Consumer<RentalHistory.Statistics> statisticsConsumer) {
        BackgroundOperation.execute(
                () -> historyService.fetchHistory(new HistoryService.Query(intervalSupplier.get())),
                result -> {
                    model.accept(result);
                    statisticsConsumer.accept(result.getStatistics());
                }
        );
    }

    private static class HistoricalRentalRenderer extends TaskRenderer.Default<HistoricalRentalsModel.HistoricalRentalAsTask> {
        public HistoricalRentalRenderer() {
            setBackground(Color.pink);
            setOpaque(true);
        }

        @Override
        protected String getTextFromTask(HistoricalRentalsModel.HistoricalRentalAsTask event) {
            return event.historicalRental.getClientName();
        }
    }

    private static class HistoricalRentalTooltipRenderer extends TaskRenderer.Default<HistoricalRentalsModel.HistoricalRentalAsTask> {
        @Override
        protected String getTextFromTask(HistoricalRentalsModel.HistoricalRentalAsTask event) {
            return "<html>" + event.historicalRental.getClientName() +
                    "<p>" + event.historicalRental.getClientEmail() +
                    "<p>" + event.historicalRental.getModel();
        }
    }
}
