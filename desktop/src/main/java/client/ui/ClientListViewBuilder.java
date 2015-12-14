package client.ui;

import client.ui.util.BackgroundOperation;
import client.ui.util.FilterableTable;
import common.domain.Client;
import common.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

import static swingutils.components.ComponentFactory.*;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;

@Component
public class ClientListViewBuilder {

    @Autowired
    ClientService clientService;
    @Autowired
    Customers customers;

    public ClientListView build() {

        FilterableTable ft = FilterableTable.create(customers);
        JComponent content = borderLayout()
                .north(
                        flowLayout(
                                button("Refresh", this::refresh),
                                button("Add...", () -> addNewClientClicked(ft.table)),
                                label("Filter:"),
                                ft.filter
                        ))
                .center(inScrollPane(ft.table))
                .build();
        refresh();
        return new ClientListView(content, ft.table);
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
