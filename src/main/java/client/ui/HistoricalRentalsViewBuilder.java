package client.ui;

import common.service.HistoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HistoricalRentalsViewBuilder {

    private HistoryService historyService;

    public JComponent build() {

        HistoricalRentalsTableModel tableModel = new HistoricalRentalsTableModel();

        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(tableModel);
        panel.add(buildToolbar(tableModel, table), BorderLayout.NORTH);
        table.setDefaultRenderer(ZonedDateTime.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, ((ZonedDateTime) value).format(DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm")), isSelected, hasFocus, row, column);
            }
        });
        panel.add(new JScrollPane(table));
        return panel;
    }

    private JComponent buildToolbar(HistoricalRentalsTableModel tableModel, JTable table) {
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
