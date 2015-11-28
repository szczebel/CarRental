package client.ui;

import common.service.HistoryService;
import org.springframework.core.convert.converter.Converter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

        panel.add(new JButton(new AbstractAction("Search") {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh(tableModel);
            }
        }));


        return panel;
    }

    private void refresh(HistoricalRentalsTableModel tableModel) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime weekAgo = now.minusDays(7);
        HistoryService.Query query = new HistoryService.Query(weekAgo, now);//todo: read from GUI
        BackgroundOperation.execute(
                () -> historyService.fetchHistory(query),
                tableModel::setData
        );
    }


    @SuppressWarnings("unused")
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }
}
