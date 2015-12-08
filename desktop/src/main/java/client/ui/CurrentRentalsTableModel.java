package client.ui;

import common.domain.CurrentRental;

import javax.swing.table.AbstractTableModel;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

class CurrentRentalsTableModel extends AbstractTableModel {

    final static String[] COLUMN = {"Registration", "Model", "Client name", "Client email", "Start", "Planned end"};
    private List<CurrentRental> currentRentals = new ArrayList<>();

    void setData(List<CurrentRental> fleet) {
        this.currentRentals = fleet;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return currentRentals.size();
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
        CurrentRental currentRental = currentRentals.get(rowIndex);
        if (columnIndex == 0) return currentRental.getRegistration();
        if (columnIndex == 1) return currentRental.getModel();
        if (columnIndex == 2) return currentRental.getClientName();
        if (columnIndex == 3) return currentRental.getClientEmail();
        if (columnIndex == 4) return currentRental.getStart();
        if (columnIndex == 5) return currentRental.getPlannedEnd();
        throw new IllegalArgumentException("Unknown column index : " + columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 4) return ZonedDateTime.class;
        if (columnIndex == 5) return ZonedDateTime.class;
        return super.getColumnClass(columnIndex);
    }

    public CurrentRental getAt(int index) {
        return currentRentals.get(index);
    }
}
