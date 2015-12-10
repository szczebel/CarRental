package client.ui;

import common.domain.Client;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

@Component
public class Customers extends AbstractTableModel {

    final static String[] COLUMN = {"Name", "Email"};
    private List<Client> clients = new ArrayList<>();
    private DefaultComboBoxModel<Client> comboBoxModel = new DefaultComboBoxModel<>();

    void setData(List<Client> clients) {
        this.clients = clients;
        comboBoxModel.removeAllElements();
        comboBoxModel.addElement(null);
        clients.forEach(comboBoxModel::addElement);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return clients.size();
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
        Client c = clients.get(rowIndex);
        if (columnIndex == 0) return c.getName();
        if (columnIndex == 1) return c.getEmail();
        throw new IllegalArgumentException("Unknown column index : " + columnIndex);
    }

    public Client getAt(int index) {
        return clients.get(index);
    }

    public ComboBoxModel<Client> getComboBoxModel() {
        return comboBoxModel;
    }
}
