package client.ui;

import common.domain.Client;
import common.service.ClientService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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

}
