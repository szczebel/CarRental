package client.ui;

import common.domain.RentalHistory;
import common.service.HistoryService;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.beans.factory.annotation.Autowired;
import schedule.interaction.InstantTooltips;
import schedule.view.ResourceRenderer;
import schedule.view.ScheduleView;
import schedule.view.TaskRenderer;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Consumer;

import static client.ui.GuiHelper.*;
import static common.util.TimeUtils.toMidnight;

@org.springframework.stereotype.Component
public class HistoricalRentalsViewBuilder {

    @Autowired HistoryService historyService;

    public JComponent build() {
        HistoricalRentalsModel model = new HistoricalRentalsModel();
        ScheduleView<CarInfo, HistoricalRentalAdapter> chart = createChart(model);
        RentalHistoryStatisticsView statisticsView = new RentalHistoryStatisticsView();

        return borderLayout()
                .north(buildToolbar(model, statisticsView))
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

    private ScheduleView<CarInfo, HistoricalRentalAdapter> createChart(HistoricalRentalsModel tableModel) {
        ScheduleView<CarInfo, HistoricalRentalAdapter> chart = new ScheduleView<>(tableModel);
        chart.setTaskRenderer(new HistoricalRentalRenderer());
        chart.setResourceRenderer(new CarInfoRenderer());
        chart.setMouseInteractions(InstantTooltips.renderWith(new HistoricalRentalTooltipRenderer()));
        return chart;
    }

    private JComponent buildToolbar(Consumer<RentalHistory> model, Consumer<RentalHistory.Statistics> statisticsConsumer) {
        UtilDateModel start = new UtilDateModel(Date.from(ZonedDateTime.now().minusDays(30).toInstant()));
        UtilDateModel end = new UtilDateModel(Date.from(ZonedDateTime.now().toInstant()));

        return toolbar(
                label("Find historical rentals from:"),
                datePicker(start),
                label("to:"),
                datePicker(end),
                button("Search", () -> searchClicked(start, end, model, statisticsConsumer))
        );
    }

    private void searchClicked(UtilDateModel start, UtilDateModel end, Consumer<RentalHistory> model, Consumer<RentalHistory.Statistics> statisticsConsumer) {
        HistoryService.Query query = new HistoryService.Query(
                toMidnight(ZonedDateTime.ofInstant(start.getValue().toInstant(), ZoneId.systemDefault())),
                toMidnight(ZonedDateTime.ofInstant(end.getValue().toInstant(), ZoneId.systemDefault()))
                );
        BackgroundOperation.execute(
                () -> historyService.fetchHistory(query),
                result -> {
                    model.accept(result);
                    statisticsConsumer.accept(result.getStatistics());
                }
        );
    }

    private static class CarInfoRenderer extends ResourceRenderer.Default<CarInfo> {
        @Override
        protected String getTextFromResource(CarInfo resource) {
            return resource.registration + " , " + resource.model;
        }
    }

    private static class HistoricalRentalRenderer extends TaskRenderer.Default<HistoricalRentalAdapter> {
        public HistoricalRentalRenderer() {
            setBackground(Color.pink);
            setOpaque(true);
        }

        @Override
        protected String getTextFromTask(HistoricalRentalAdapter event) {
            return event.historicalRental.getClientName();
        }
    }

    private static class HistoricalRentalTooltipRenderer extends TaskRenderer.Default<HistoricalRentalAdapter> {
        @Override
        protected String getTextFromTask(HistoricalRentalAdapter event) {
            return "<html>" + event.historicalRental.getClientName() +
                    "<p>" + event.historicalRental.getClientEmail() +
                    "<p>" + event.historicalRental.getModel();
        }
    }
}
