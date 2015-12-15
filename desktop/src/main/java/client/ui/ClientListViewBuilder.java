package client.ui;

import client.ui.util.BackgroundOperation;
import common.domain.Client;
import common.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.components.table.TablePanel;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

import static swingutils.components.ComponentFactory.button;

@Component
public class ClientListViewBuilder {

    @Autowired
    ClientService clientService;

    public TablePanel<Client> build() {
        Customers customers = new Customers();
        TablePanel<Client> table = customers.createTable();
        table.getToolbar().add(button("Add...", () -> addNewClientClicked(table.getToolbar(), customers::setData)), 0);
        table.getToolbar().add(button("Refresh", () -> refresh(customers::setData)), 0);
        refresh(customers::setData);
        return table;
    }

    private void addNewClientClicked(JComponent panel, Consumer<List<Client>> customers) {
        String name = JOptionPane.showInputDialog(panel, "Name", "Add new client", JOptionPane.QUESTION_MESSAGE);
        String phone = JOptionPane.showInputDialog(panel, "Phone", "Add new client", JOptionPane.QUESTION_MESSAGE);
        BackgroundOperation.execute(
                () -> clientService.create(new Client(name, phone)),
                () -> refresh(customers)
        );
    }

    private void refresh(Consumer<List<Client>> customers) {
        BackgroundOperation.execute(
                clientService::fetchAll,
                customers
        );
    }
}
