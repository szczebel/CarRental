package client.ui;

import common.domain.RentalClass;
import common.domain.RentalHistory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Consumer;

import static client.ui.GuiHelper.grid;
import static client.ui.GuiHelper.inScrollPane;

public class RentalHistoryStatisticsView implements Consumer<RentalHistory.Statistics> {

    JComponent component;
    SummaryStatsTableModel summaryStatsTableModel = new SummaryStatsTableModel();
    UtilizationPerDayOfWeekTableModel utilizationPerDayOfWeek = new UtilizationPerDayOfWeekTableModel();


    public RentalHistoryStatisticsView() {

        component = grid(2, 1,
                inScrollPane(createTable(summaryStatsTableModel)),
                inScrollPane(createTable(utilizationPerDayOfWeek))
        );
    }

    private JTable createTable(TableModel tableModel) {
        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(Double.class, GuiHelper.convertingRenderer(value -> String.format("%.2f", (double)value)));
        return table;
    }

    @Override
    public void accept(RentalHistory.Statistics statistics) {
        summaryStatsTableModel.setData(statistics);
        utilizationPerDayOfWeek.setData(statistics.getUtilizationPerDayOfWeek());
    }


    public JComponent getComponent() {
        return component;
    }

    static class UtilizationPerDayOfWeekTableModel extends AbstractTableModel {

        Map<DayOfWeek, RentalHistory.ValuePerClass> data = new HashMap<>();
        java.util.List<String> rentalClasses = new ArrayList<>();


        public void setData(Map<DayOfWeek, RentalHistory.ValuePerClass> dayOfWeekAverages) {
            data = dayOfWeekAverages;
            Set<String> uniqueClasses = new HashSet<>();
            data.values().forEach(vpc -> uniqueClasses.addAll(vpc.keySet()));
            rentalClasses = new ArrayList<>(uniqueClasses);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rentalClasses.size();
        }

        @Override
        public int getColumnCount() {
            return DayOfWeek.values().length + 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            String className = rentalClasses.get(rowIndex);
            if (columnIndex == 0) return className;
            DayOfWeek dow = DayOfWeek.values()[columnIndex - 1];
            return data.get(dow).getValueFor(className);
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) return "Rental class";
            return DayOfWeek.values()[column - 1].getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return String.class;
            return Double.class;
        }
    }

    static class SummaryStatsTableModel extends AbstractTableModel {

        static final String[] COLUMNS = {"Rental class", "Earnings", "Utilization"};
        java.util.List<String> rentalClasses = Collections.emptyList();
        RentalHistory.Statistics statistics = new RentalHistory.Statistics(Collections.<RentalClass>emptyList(), 0, 0);

        public void setData(RentalHistory.Statistics statistics) {
            this.statistics = statistics;
            Set<String> uniqueClasses = new HashSet<>();
            uniqueClasses.addAll(statistics.getEarningsPerClass().keySet());
            uniqueClasses.addAll(statistics.getUtilizationPerClass().keySet());
            rentalClasses = new ArrayList<>(uniqueClasses);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rentalClasses.size() + 1;
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex == rentalClasses.size()) {
                if (columnIndex == 0) return "Total:";
                if (columnIndex == 1) return statistics.getOverallEarnings();
                if (columnIndex == 2) return statistics.getOverallUtilization();
            } else {
                String className = rentalClasses.get(rowIndex);
                if (columnIndex == 0) return className;
                if (columnIndex == 1) return statistics.getEarningsPerClass().getValueFor(className);
                if (columnIndex == 2) return statistics.getUtilizationPerClass().getValueFor(className);
            }
            throw new IllegalArgumentException("?? " + rowIndex + " , " + columnIndex);
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return String.class;
            return Double.class;
        }
    }
}
