package client.ui.booking;

import client.ui.FleetCache;
import common.domain.Booking;

import javax.swing.table.AbstractTableModel;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class BookingsModel extends AbstractTableModel {

    final FleetCache fleetCache;
    final static String[] COLUMN = {"Registration", "Model", "Client name", "Client email", "Start", "End"};
    private List<Booking> records = new ArrayList<>();

    BookingsModel(FleetCache fleetCache) {
        this.fleetCache = fleetCache;
    }

    void setData(Collection<Booking> bookings) {
        this.records.clear();
        this.records.addAll(bookings);//todo sort
        fireTableDataChanged();
    }


    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Booking event = records.get(rowIndex);
        if (columnIndex == 0) return event.getRegistration();
        if (columnIndex == 1) return event.getModel();
        if (columnIndex == 2) return event.getClientName();
        if (columnIndex == 3) return event.getClientEmail();
        if (columnIndex == 4) return event.getStart();
        if (columnIndex == 5) return event.getEnd();
        throw new IllegalArgumentException("Unknown column index : " + columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 4 || columnIndex == 5) return ZonedDateTime.class;
        return super.getColumnClass(columnIndex);
    }

    Booking getBooking(int index) {
        return records.get(index);
    }
}
