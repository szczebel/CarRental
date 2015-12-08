package client.ui;

import common.domain.Client;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class ClientListTableModel extends AbstractTableModel {

    final static String[] COLUMN = {"Name", "Email"};
    private List<Client> clients = new ArrayList<>();

    void setData(List<Client> clients) {
        this.clients = clients;
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

    public Client getClientAt(int index) {
        return clients.get(index);
    }
}
