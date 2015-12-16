package client.ui.booking;

import common.domain.AbstractAssignment;
import common.domain.Booking;
import swingutils.EventListHolder;
import swingutils.components.table.TableFactory;
import swingutils.components.table.TablePanel;
import swingutils.components.table.descriptor.Columns;

import java.time.format.DateTimeFormatter;

class Bookings extends EventListHolder<Booking> {

    TablePanel<Booking> createTable() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm");
        Columns<Booking> columns = Columns.create(Booking.class)
                .column("Registration", String.class, AbstractAssignment::getRegistration)
                .column("Model", String.class, AbstractAssignment::getModel)
                .column("Customer name", String.class, AbstractAssignment::getClientName)
                .column("Customer email", String.class, AbstractAssignment::getClientEmail)
                .column("Start", String.class, cr -> cr.getStart().format(formatter))
                .column("End", String.class, cr -> cr.getEnd().format(formatter))
                ;
        return TableFactory.createTablePanel(getData(), columns);
    }
}
