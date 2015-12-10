package client.ui.history;

import client.ui.FleetCache;
import client.ui.scheduleview.CarResource;
import common.domain.HistoricalRental;
import common.domain.RentalHistory;
import schedule.basic.GenericScheduleModel;
import schedule.model.ScheduleModel;

import javax.swing.table.AbstractTableModel;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class HistoricalRentalsModel extends AbstractTableModel {

    final static String[] COLUMN = {"Registration", "Model", "Client name", "Client email", "Start", "End", "Duration"};
    final FleetCache fleetCache;
    private List<HistoricalRental> records = new ArrayList<>();
    private GenericScheduleModel<CarResource, HistoricalRentalAsTask> delegate = new GenericScheduleModel<>();

    HistoricalRentalsModel(FleetCache fleetCache) {
        this.fleetCache = fleetCache;
        delegate.addResources(fleetCache.getFleet().stream().map(CarResource::new).collect(Collectors.toSet()));
    }

    void setData(RentalHistory rentalHistory) {
        this.records = rentalHistory.getRecords();
        fireTableDataChanged();
        delegate.clearAllData();
        delegate.addResources(fleetCache.getFleet().stream().map(CarResource::new).collect(Collectors.toSet()));
        delegate.assignAll(records.stream().map(HistoricalRentalAsTask::new), t -> new CarResource(t.getAbstractAssignment()));
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

    public ScheduleModel<CarResource, HistoricalRentalAsTask> asScheduleModel() {
        return delegate;
    }
}
