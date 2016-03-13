package client.ui;

import common.domain.Client;
import common.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.background.BackgroundOperation;
import swingutils.components.progress.ProgressIndicator;
import swingutils.components.table.TablePanel;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

import static swingutils.components.ComponentFactory.button;

@Component
public class ClientListViewBuilder {

    @Autowired
    ClientService clientService;

    public TablePanel<Client> build(ProgressIndicator pi) {
        Customers customers = new Customers();
        TablePanel<Client> table = customers.createTable();
        table.getToolbar().add(button("Add...", () -> addNewClientClicked(table.getToolbar(), customers::setData, pi)), 0);
        table.getToolbar().add(button("Refresh", () -> refresh(customers::setData, pi)), 0);
        refresh(customers::setData, pi);
        return table;
    }

    private void addNewClientClicked(JComponent panel, Consumer<List<Client>> customers, ProgressIndicator pi) {
        String name = JOptionPane.showInputDialog(panel, "Name", "Add new client", JOptionPane.QUESTION_MESSAGE);
        String email = JOptionPane.showInputDialog(panel, "Email", "Add new client", JOptionPane.QUESTION_MESSAGE);
        BackgroundOperation.execute(
                () -> clientService.create(new Client(name, email)),
                () -> refresh(customers, pi)
        );
    }

    private void refresh(Consumer<List<Client>> customers, ProgressIndicator pi) {
        BackgroundOperation.execute(
                clientService::fetchAll,
                customers,
                pi
        );
    }
}
