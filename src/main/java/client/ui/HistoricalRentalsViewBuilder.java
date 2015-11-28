package client.ui;

import common.service.HistoryService;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.core.convert.converter.Converter;

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

        HistoricalRentalsTableModel tableModel = new HistoricalRentalsTableModel();

        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(tableModel);
        panel.add(buildToolbar(tableModel), BorderLayout.NORTH);
        table.setDefaultRenderer(ZonedDateTime.class, convertingRenderer(value -> ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm"))));
        table.setDefaultRenderer(Duration.class, convertingRenderer(value -> ((Duration) value).toHours() + " hours"));
        panel.add(new JScrollPane(table));
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

    private JComponent buildToolbar(HistoricalRentalsTableModel tableModel) {
        JPanel panel = new JPanel();


        UtilDateModel start = new UtilDateModel(Date.from(ZonedDateTime.now().minusDays(30).toInstant()));
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


    @SuppressWarnings("unused")
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }
}
