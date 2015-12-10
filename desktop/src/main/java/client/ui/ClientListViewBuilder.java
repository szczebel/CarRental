package client.ui;

import client.ui.util.BackgroundOperation;
import common.domain.Client;
import common.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

import static client.ui.util.GuiHelper.*;

@Component
public class ClientListViewBuilder {

    @Autowired ClientService clientService;
    @Autowired Customers customers;

    public JComponent build() {

        JTable table = new JTable(customers);
        refresh();

        return borderLayout()
                .north(
                        toolbar(
                                button("Refresh", this::refresh),
                                button("Add...", () -> addNewClientClicked(table))
                        ))
                .center(inScrollPane(table))
                .build();
    }

    private void addNewClientClicked(JComponent panel) {
        String name = JOptionPane.showInputDialog(panel, "Name", "Add new client", JOptionPane.QUESTION_MESSAGE);
        String phone = JOptionPane.showInputDialog(panel, "Phone", "Add new client", JOptionPane.QUESTION_MESSAGE);
        BackgroundOperation.execute(
                () -> clientService.create(new Client(name, phone)),
                this::refresh
        );
    }

    private void refresh() {
        BackgroundOperation.execute(
                clientService::fetchAll,
                customers::setData
        );
    }
}
