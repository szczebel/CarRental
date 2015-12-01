package client.ui;

import common.service.HistoryService;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.beans.factory.annotation.Autowired;
import schedule.chart.ResourceRenderer;
import schedule.chart.ScheduleChart;
import schedule.chart.TaskRenderer;
import schedule.interaction.Tooltips;

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
        ScheduleChart<CarInfo, HistoricalRentalAdapter> chart = createChart(model);
        JTable table = createTable(model);

        return borderLayout()
                .north(buildToolbar(model))
                .center(
                        tabbedPane(SwingUtilities.BOTTOM)
                        .addTab("Table", inScrollPane(table))
                        .addTab("Chart", chart.getComponent())
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

    private ScheduleChart<CarInfo, HistoricalRentalAdapter> createChart(HistoricalRentalsModel tableModel) {
        ScheduleChart<CarInfo, HistoricalRentalAdapter> chart = new ScheduleChart<>(tableModel);
        chart.setTaskRenderer(new HistoricalRentalRenderer());
        chart.setResourceRenderer(new CarInfoRenderer());
        chart.setInteractions(Tooltips.with(new HistoricalRentalTooltipRenderer()));
        return chart;
    }

    private JComponent buildToolbar(HistoricalRentalsModel model) {
        UtilDateModel start = new UtilDateModel(Date.from(ZonedDateTime.now().minusDays(30).toInstant()));
        UtilDateModel end = new UtilDateModel(Date.from(ZonedDateTime.now().toInstant()));

        return toolbar(
                label("Find historical rentals from:"),
                datePicker(start),
                label("to:"),
                datePicker(end),
                button("Search", () -> searchClicked(start, end, model))
        );
    }

    private void searchClicked(UtilDateModel start, UtilDateModel end, HistoricalRentalsModel model) {
        HistoryService.Query query = new HistoryService.Query(
                ZonedDateTime.ofInstant(start.getValue().toInstant(), ZoneId.systemDefault()),
                ZonedDateTime.ofInstant(end.getValue().toInstant(), ZoneId.systemDefault())
        );
        BackgroundOperation.execute(
                () -> historyService.fetchHistory(query),
                model::setData
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
