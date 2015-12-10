package client.ui;

import common.domain.RentalClass;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

@Component
public class RentalClasses extends AbstractTableModel {

    final static String[] COLUMN = {"Name", "Hourly rate"};
    private List<RentalClass> data = new ArrayList<>();
    private DefaultComboBoxModel<RentalClass> comboBoxModel = new DefaultComboBoxModel<>();

    void setData(List<RentalClass> data) {
        this.data = data;
        comboBoxModel.removeAllElements();
        comboBoxModel.addElement(null);
        data.forEach(comboBoxModel::addElement);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
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
        RentalClass aClass = data.get(rowIndex);
        if (columnIndex == 0) return aClass.getName();
        if (columnIndex == 1) return aClass.getHourlyRate();
        throw new IllegalArgumentException("Unknown column index : " + columnIndex);
    }

    public ComboBoxModel<RentalClass> getComboBoxModel() {
        return comboBoxModel;
    }
}
