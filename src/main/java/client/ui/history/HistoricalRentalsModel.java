package client.ui.history;

import client.ui.util.CarResource;
import client.ui.util.FleetCache;
import common.domain.HistoricalRental;
import common.domain.RentalHistory;
import schedule.basic.BasicScheduleModel;
import schedule.model.ScheduleModel;

import javax.swing.table.AbstractTableModel;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class HistoricalRentalsModel extends AbstractTableModel implements ScheduleModel<CarResource, HistoricalRentalAsTask> {

    final static String[] COLUMN = {"Registration", "Model", "Client name", "Client email", "Start", "End", "Duration"};
    final FleetCache fleetCache;
    private List<HistoricalRental> records = new ArrayList<>();
    private BasicScheduleModel<CarResource, HistoricalRentalAsTask> delegate = new BasicScheduleModel<>();
    private ScheduleModel.Listener listener;

    HistoricalRentalsModel(FleetCache fleetCache) {
        this.fleetCache = fleetCache;
        delegate.addResources(fleetCache.getFleet().stream().map(CarResource::new).collect(Collectors.toSet()));
    }

    public Consumer<RentalHistory> asConsumer() {
        return this::setData;
    }

    void setData(RentalHistory rentalHistory) {
        this.records = rentalHistory.getRecords();
        delegate.clearAllData();
        delegate.addResources(fleetCache.getFleet().stream().map(CarResource::new).collect(Collectors.toSet()));
        records.forEach(hr -> delegate.assign(new CarResource(hr), new HistoricalRentalAsTask(hr)));
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
        HistoricalRental event = records.get(rowIndex);
        if (columnIndex == 0) return event.getRegistration();
        if (columnIndex == 1) return event.getModel();
        if (columnIndex == 2) return event.getClientName();
        if (columnIndex == 3) return event.getClientEmail();
        if (columnIndex == 4) return event.getStart();
        if (columnIndex == 5) return event.getEnd();
        if (columnIndex == 6) return event.getDuration();
        throw new IllegalArgumentException("Unknown column index : " + columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 4 || columnIndex == 5) return ZonedDateTime.class;
        if (columnIndex == 6) return Duration.class;
        return super.getColumnClass(columnIndex);
    }

    @Override
    public List<CarResource> getResources() {
        return delegate.getResources();
    }

    @Override
    public Collection<HistoricalRentalAsTask> getEventsAssignedTo(CarResource carResource) {
        return delegate.getEventsAssignedTo(carResource);
    }

    @Override
    public void setListener(ScheduleModel.Listener listener) {
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


}
