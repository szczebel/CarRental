package client.ui;

import common.domain.Client;
import common.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

import static client.ui.GuiHelper.*;

@Component
public class ClientListViewBuilder {

    @Autowired
    ClientService clientService;

    public JComponent build() {

        ClientListTableModel tableModel = new ClientListTableModel();
        JTable table = new JTable(tableModel);

        refresh(tableModel);
        return borderLayout()
                .north(
                        toolbar(
                                button("Refresh", () -> refresh(tableModel)),
                                button("Add...", () -> addNewClientClicked(table, tableModel))
                        ))
                .center(inScrollPane(table))
                .get();
    }

    private void addNewClientClicked(JComponent panel, ClientListTableModel tableModel) {
        String name = JOptionPane.showInputDialog(panel, "Name", "Add new client", JOptionPane.QUESTION_MESSAGE);
        String phone = JOptionPane.showInputDialog(panel, "Phone", "Add new client", JOptionPane.QUESTION_MESSAGE);
        BackgroundOperation.execute(
                () -> clientService.create(new Client(name, phone)),
                () -> refresh(tableModel)
        );
    }

    private void refresh(ClientListTableModel tableModel) {
        BackgroundOperation.execute(
                clientService::fetchAll,
                tableModel::setData
        );
    }
}
