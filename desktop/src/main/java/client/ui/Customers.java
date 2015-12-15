package client.ui;

import ca.odell.glazedlists.EventList;
import common.domain.Client;
import swingutils.components.table.TableFactory;
import swingutils.components.table.TablePanel;
import swingutils.components.table.descriptor.Columns;

import java.util.List;

import static swingutils.EventListHolder.clearEventList;
import static swingutils.EventListHolder.eventList;

public class Customers {

    private EventList<Client> clients = eventList();

    void setData(List<Client> clients) {
        clearEventList(this.clients);
        this.clients.addAll(clients);
    }

    public TablePanel<Client> createTable() {
        Columns<Client> columns = Columns.create(Client.class)
                .column("Name", String.class, Client::getName)
                .column("Email", String.class, Client::getEmail);
        return TableFactory.createTablePanel(clients, columns);
    }
}
