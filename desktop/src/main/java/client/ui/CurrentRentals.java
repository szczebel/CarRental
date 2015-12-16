package client.ui;

import common.domain.AbstractAssignment;
import common.domain.CurrentRental;
import swingutils.EventListHolder;
import swingutils.components.table.TableFactory;
import swingutils.components.table.TablePanel;
import swingutils.components.table.descriptor.Columns;

import java.time.format.DateTimeFormatter;

class CurrentRentals extends EventListHolder<CurrentRental> {

    TablePanel<CurrentRental> createTable() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm");
        Columns<CurrentRental> columns = Columns.create(CurrentRental.class)
                .column("Registration", String.class, AbstractAssignment::getRegistration)
                .column("Model", String.class, AbstractAssignment::getModel)
                .column("Customer name", String.class, AbstractAssignment::getClientName)
                .column("Customer email", String.class, AbstractAssignment::getClientEmail)
                .column("Start", String.class, cr -> cr.getStart().format(formatter))
                .column("Planned End", String.class, cr -> cr.getEnd().format(formatter))
                ;
        return TableFactory.createTablePanel(getData(), columns);
    }
}
