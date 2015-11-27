package client.ui;

import common.domain.Client;
import common.service.ClientService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ClientListViewBuilder {

    private ClientService clientService;

    public JComponent build() {

        ClientListTableModel tableModel = new ClientListTableModel();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildToolbar(tableModel), BorderLayout.NORTH);
        panel.add(new JScrollPane(buildTable(tableModel)));

        return panel;
    }

    private JComponent buildTable(ClientListTableModel tableModel) {
        return new JTable(tableModel);
    }

    private JComponent buildToolbar(ClientListTableModel tableModel) {
        JPanel panel = new JPanel();

        panel.add(new JButton(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                BackgroundOperation.execute(
                        clientService::fetchAll,
                        tableModel::setData
                );
            }
        }));

        panel.add(new JButton(new AbstractAction("Add...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(panel, "Name", "Add new client", JOptionPane.QUESTION_MESSAGE);
                String phone = JOptionPane.showInputDialog(panel, "Phone", "Add new client", JOptionPane.QUESTION_MESSAGE);
                BackgroundOperation.execute(
                        () -> clientService.create(new Client(name, phone)),
                        tableModel::add
                );
            }
        }));


        return panel;
    }

    @SuppressWarnings("unused")
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }


    static class ClientListTableModel extends AbstractTableModel {

        final static String[] COLUMN = {"Name", "Phone"};
        private List<Client> clients = new ArrayList<>();

        void setData(List<Client> clients) {
            this.clients = clients;
            fireTableStructureChanged();
        }

        void add(Client client) {
            clients.add(client);
            fireTableStructureChanged();
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
    }
}
