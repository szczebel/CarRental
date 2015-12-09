package client.ui.booking;

import client.ui.util.CarResource;
import client.ui.util.FleetCache;
import common.domain.Booking;
import schedule.basic.BasicScheduleModel;
import schedule.model.ScheduleModel;

import javax.swing.table.AbstractTableModel;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class BookingsModel extends AbstractTableModel implements ScheduleModel<CarResource, BookingAsTask> {

    final FleetCache fleetCache;
    final static String[] COLUMN = {"Registration", "Model", "Client name", "Client email", "Start", "End"};
    private List<Booking> records = new ArrayList<>();
    private BasicScheduleModel<CarResource, BookingAsTask> delegate = new BasicScheduleModel<>();
    private Listener listener;

    BookingsModel(FleetCache fleetCache) {
        this.fleetCache = fleetCache;
        delegate.addResources(fleetCache.getFleet().stream().map(CarResource::new).collect(Collectors.toSet()));
    }

    void setData(Collection<Booking> bookings) {
        this.records.clear();
        this.records.addAll(bookings);//todo sort
        delegate.clearAllData();
        delegate.addResources(fleetCache.getFleet().stream().map(CarResource::new).collect(Collectors.toSet()));

        records.forEach(hr -> delegate.assign(new CarResource(hr), new BookingAsTask(hr)));
        fireTableDataChanged();
        listener.dataChanged();
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

    @Override
    public List<CarResource> getResources() {
        return delegate.getResources();
    }

    @Override
    public Collection<BookingAsTask> getEventsAssignedTo(CarResource carResource) {
        return delegate.getEventsAssignedTo(carResource);
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public ZonedDateTime getEnd() {
        return delegate.getEnd();
    }

    @Override
    public ZonedDateTime getStart() {
        return delegate.getStart();
    }

    public Consumer<Collection<Booking>> asConsumer() {
        return this::setData;
    }

    Booking getBooking(int index) {
        return records.get(index);
    }
}
