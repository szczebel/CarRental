package client.ui;

import common.service.HistoryService;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.core.convert.converter.Converter;
import schedule.chart.ScheduleChart;

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

public class HistoricalRentalsViewBuilder {

    private HistoryService historyService;

    public JComponent build() {

        HistoricalRentalsModel tableModel = new HistoricalRentalsModel();
        ScheduleChart<CarInfo, HistoricalRentalAdapter> chart = new ScheduleChart<>(tableModel, ZonedDateTime.now().minusDays(60), ZonedDateTime.now());


        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(ZonedDateTime.class, convertingRenderer(value -> ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm"))));
        table.setDefaultRenderer(Duration.class, convertingRenderer(value -> ((Duration) value).toHours() + " hours"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildToolbar(tableModel, chart), BorderLayout.NORTH);
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

    private JComponent buildToolbar(HistoricalRentalsModel tableModel, ScheduleChart<CarInfo, HistoricalRentalAdapter> chart) {
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
                //todo: update chart.start and chart.end
                BackgroundOperation.execute(
                        () -> historyService.fetchHistory(query),
                        tableModel::setData
                );
            }
        }));


        return panel;
    }


    @SuppressWarnings("unused")
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }
}
