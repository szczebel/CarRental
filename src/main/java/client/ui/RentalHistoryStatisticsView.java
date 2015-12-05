package client.ui;

import common.domain.RentalHistory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Consumer;

import static client.ui.GuiHelper.grid;
import static client.ui.GuiHelper.inScrollPane;

public class RentalHistoryStatisticsView implements Consumer<RentalHistory.Statistics> {

    JComponent component;
    SummaryStatsTableModel summaryStatsTableModel = new SummaryStatsTableModel();
    DayOfWeekStatsTableModel averageHours = new DayOfWeekStatsTableModel("Average hours");
    DayOfWeekStatsTableModel averageEarnings = new DayOfWeekStatsTableModel("Average earnings");


    public RentalHistoryStatisticsView() {

        component = grid(3, 1,
                inScrollPane(new JTable(summaryStatsTableModel)),
                inScrollPane(new JTable(averageHours)),
                inScrollPane(new JTable(averageEarnings))
        );
    }

    @Override
    public void accept(RentalHistory.Statistics statistics) {
        summaryStatsTableModel.setData(statistics);
        averageHours.setData(statistics.getDayOfWeekHoursAverages());
        averageEarnings.setData(statistics.getDayOfWeekProfitAverages());
    }


    public JComponent getComponent() {
        return component;
    }

    static class DayOfWeekStatsTableModel extends AbstractTableModel {

        private final String firstColumnHeader;
        Map<DayOfWeek, RentalHistory.DataPoint> data = new HashMap<>();
        java.util.List<String> rentalClasses = new ArrayList<>();

        public DayOfWeekStatsTableModel(String firstColumnHeader) {
            this.firstColumnHeader = firstColumnHeader;
        }

        public void setData(Map<DayOfWeek, RentalHistory.DataPoint> dayOfWeekAverages) {
            data = dayOfWeekAverages;
            Set<String> uniqueClasses = new HashSet<>();
            data.values().forEach(dp -> uniqueClasses.addAll(dp.getPerClass().keySet()));
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
            return data.get(dow).getPerClass().get(className);
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) return firstColumnHeader;
            return DayOfWeek.values()[column - 1].getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        }
    }

    static class SummaryStatsTableModel extends AbstractTableModel {

        static final String[] COLUMNS = {"Rental class", "Sum of hours", "Sum of earnings"};
        RentalHistory.DataPoint hoursSummary = new RentalHistory.DataPoint(Collections.emptyList());
        RentalHistory.DataPoint profitSummary = new RentalHistory.DataPoint(Collections.emptyList());
        java.util.List<String> rentalClasses = new ArrayList<>();

        public void setData(RentalHistory.Statistics statistics) {
            hoursSummary = statistics.getHoursSummary();
            profitSummary = statistics.getProfitSummary();
            Set<String> uniqueClasses = new HashSet<>();
            uniqueClasses.addAll(hoursSummary.getPerClass().keySet());
            uniqueClasses.addAll(profitSummary.getPerClass().keySet());
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
                if (columnIndex == 1) return hoursSummary.getOverall();
                if (columnIndex == 2) return profitSummary.getOverall();
            } else {
                String className = rentalClasses.get(rowIndex);
                if (columnIndex == 0) return className;
                if (columnIndex == 1) return hoursSummary.getPerClass().get(className);
                if (columnIndex == 2) return profitSummary.getPerClass().get(className);
            }
            throw new IllegalArgumentException("?? " + rowIndex + " , " + columnIndex);
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }
    }
}
