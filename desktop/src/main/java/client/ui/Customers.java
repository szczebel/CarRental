package client.ui;

import client.PubSub;
import common.domain.Client;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import swingutils.EventListHolder;
import swingutils.components.table.TableFactory;
import swingutils.components.table.TablePanel;
import swingutils.components.table.descriptor.Columns;

import javax.swing.*;

@Component
public class Customers extends EventListHolder<Client> {

    private Customers(){}

    public TablePanel<Client> createTable() {
        Columns<Client> columns = Columns.create(Client.class)
                .column("Name", String.class, Client::getName)
                .column("Email", String.class, Client::getEmail);
        return TableFactory.createTablePanel(getData(), columns);
    }

    @JmsListener(destination = PubSub.NEW_CLIENT_CHANNEL_TOPIC)
    void onNewClient(Client client) {
        SwingUtilities.invokeLater(() -> addClient(client));
    }

    void addClient(Client client) {
        getData().add(client);
    }
}
