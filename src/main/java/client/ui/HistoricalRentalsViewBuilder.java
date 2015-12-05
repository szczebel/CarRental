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

import static client.ui.GuiHelper.*;

@org.springframework.stereotype.Component
public class HistoricalRentalsViewBuilder {

    @Autowired HistoryService historyService;

    public JComponent build() {
        HistoricalRentalsModel model = new HistoricalRentalsModel();
        ScheduleView<CarInfo, HistoricalRentalAdapter> chart = createChart(model);
        JTable table = createTable(model);
        JTextArea statisticsView = new JTextArea("no data");
        statisticsView.setEditable(false);

        return borderLayout()
                .north(buildToolbar(model, statisticsView))
                .center(
                        tabbedPane(SwingUtilities.BOTTOM)
                        .addTab("Table", inScrollPane(table))
                        .addTab("Chart", chart.getComponent())
                        .addTab("Statistics", inScrollPane(statisticsView))
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

    private JComponent buildToolbar(HistoricalRentalsModel model, JTextArea statisticsView) {
        UtilDateModel start = new UtilDateModel(Date.from(ZonedDateTime.now().minusDays(30).toInstant()));
        UtilDateModel end = new UtilDateModel(Date.from(ZonedDateTime.now().toInstant()));

        return toolbar(
                label("Find historical rentals from:"),
                datePicker(start),
                label("to:"),
                datePicker(end),
                button("Search", () -> searchClicked(start, end, model, statisticsView))
        );
    }

    private void searchClicked(UtilDateModel start, UtilDateModel end, HistoricalRentalsModel model, JTextArea statisticsView) {
        HistoryService.Query query = new HistoryService.Query(
                ZonedDateTime.ofInstant(start.getValue().toInstant(), ZoneId.systemDefault()),
                ZonedDateTime.ofInstant(end.getValue().toInstant(), ZoneId.systemDefault())
        );
        BackgroundOperation.execute(
                () -> historyService.fetchHistory(query),
                result -> {model.setData(result.getRecords());statisticsView.setText(asString(result.getStatistics()));}
        );
    }

    private String asString(RentalHistory.Statistics statistics) {
        return "got some data";
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
