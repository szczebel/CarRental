package client.ui;

import common.domain.HistoricalRental;

import javax.swing.table.AbstractTableModel;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

class HistoricalRentalsTableModel extends AbstractTableModel {

    final static String[] COLUMN = {"Registration", "Model", "Client name", "Client email", "Start", "End"};
    private List<HistoricalRental> history = new ArrayList<>();

    void setData(List<HistoricalRental> history) {
        this.history = history;
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        return history.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        HistoricalRental event = history.get(rowIndex);
        if (columnIndex == 0) return event.getRegistration();
        if (columnIndex == 1) return event.getModel();
        if (columnIndex == 2) return event.getClientName();
        if (columnIndex == 3) return event.getClientEmail();
        if (columnIndex == 4) return event.getStart();
        if (columnIndex == 5) return event.getEnd();
        throw new IllegalArgumentException("Unknown column index : " + columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 4 || columnIndex == 5) return ZonedDateTime.class;
        return super.getColumnClass(columnIndex);
    }
}
