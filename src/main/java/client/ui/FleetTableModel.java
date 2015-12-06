package client.ui;

import common.domain.Car;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class FleetTableModel extends AbstractTableModel {

    final static String[] COLUMN = {"Registration", "Model", "Class"};
    private List<Car> fleet = new ArrayList<>();

    void setData(List<Car> fleet) {
        this.fleet = fleet;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return fleet.size();
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
        Car car = fleet.get(rowIndex);
        if (columnIndex == 0) return car.getRegistration();
        if (columnIndex == 1) return car.getModel();
        if (columnIndex == 2) return car.getRentalClassName();
        throw new IllegalArgumentException("Unknown column index : " + columnIndex);
    }

    public Car getCarAt(int index) {
        return fleet.get(index);
    }
}
