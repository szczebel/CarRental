package client.ui;

import common.domain.Client;
import swingutils.EventListHolder;
import swingutils.components.table.TableFactory;
import swingutils.components.table.TablePanel;
import swingutils.components.table.descriptor.Columns;

public class Customers extends EventListHolder<Client> {

    public TablePanel<Client> createTable() {
        Columns<Client> columns = Columns.create(Client.class)
                .column("Name", String.class, Client::getName)
                .column("Email", String.class, Client::getEmail);
        return TableFactory.createTablePanel(getData(), columns);
    }
}
