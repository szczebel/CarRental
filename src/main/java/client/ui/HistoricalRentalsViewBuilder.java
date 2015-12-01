package client.ui;

import common.service.HistoryService;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import schedule.chart.ResourceRenderer;
import schedule.chart.ScheduleChart;
import schedule.chart.TaskRenderer;
import schedule.interaction.Tooltips;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@org.springframework.stereotype.Component
public class HistoricalRentalsViewBuilder {

    @Autowired
    HistoryService historyService;

    public JComponent build() {

        HistoricalRentalsModel tableModel = new HistoricalRentalsModel();
        ScheduleChart<CarInfo, HistoricalRentalAdapter> chart = new ScheduleChart<>(tableModel);
        chart.setTaskRenderer(new HistoricalRentalRenderer());
        chart.setResourceRenderer(new CarInfoRenderer());
        chart.setInteractions(Tooltips.with(new HistoricalRentalTooltipRenderer()));

        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(ZonedDateTime.class, convertingRenderer(value -> ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm"))));
        table.setDefaultRenderer(Duration.class, convertingRenderer(value -> ((Duration) value).toHours() + " hours"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildToolbar(tableModel), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane(SwingConstants.BOTTOM);
        panel.add(tabs);

        tabs.addTab("Table", new JScrollPane(table));
        tabs.addTab("Chart", chart.getComponent());


        return panel;
    }

    private TableCellRenderer convertingRenderer(Converter<Object, Object> converter) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, converter.convert(value), isSelected, hasFocus, row, column);
            }
        };
    }

    private JComponent buildToolbar(HistoricalRentalsModel tableModel) {
        JPanel panel = new JPanel();


        UtilDateModel start = new UtilDateModel(Date.from(ZonedDateTime.now().minusDays(60).toInstant()));
        UtilDateModel end = new UtilDateModel(Date.from(ZonedDateTime.now().toInstant()));
        panel.add(new JLabel("Find historical rentals from:"));
        panel.add(new JDatePickerImpl(new JDatePanelImpl(start)));
        panel.add(new JLabel("to:"));
        panel.add(new JDatePickerImpl(new JDatePanelImpl(end)));

        panel.add(new JButton(new AbstractAction("Search") {
            @Override
            public void actionPerformed(ActionEvent e) {

                HistoryService.Query query = new HistoryService.Query(
                        ZonedDateTime.ofInstant(start.getValue().toInstant(), ZoneId.systemDefault()),
                        ZonedDateTime.ofInstant(end.getValue().toInstant(), ZoneId.systemDefault())
                );
                BackgroundOperation.execute(
                        () -> historyService.fetchHistory(query),
                        tableModel::setData
                );
            }
        }));


        return panel;
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
        public HistoricalRentalTooltipRenderer() {
        }

        @Override
        protected String getTextFromTask(HistoricalRentalAdapter event) {
            return "<html>" + event.historicalRental.getClientName() +
                    "<p>" + event.historicalRental.getClientEmail() +
                    "<p>" + event.historicalRental.getModel();
        }
    }
}
