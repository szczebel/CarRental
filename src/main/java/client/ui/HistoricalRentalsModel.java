package client.ui;

import common.domain.HistoricalRental;
import schedule.model.BasicScheduleModel;
import schedule.model.Resource;
import schedule.model.ScheduleModel;
import schedule.model.Task;

import javax.swing.table.AbstractTableModel;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class HistoricalRentalsModel extends AbstractTableModel implements ScheduleModel<CarInfo, HistoricalRentalAdapter> {

    final static String[] COLUMN = {"Registration", "Model", "Client name", "Client email", "Start", "End", "Duration"};
    private List<HistoricalRental> history = new ArrayList<>();
    private BasicScheduleModel<CarInfo, HistoricalRentalAdapter> delegate = new BasicScheduleModel<>();
    private ScheduleModel.Listener listener;

    void setData(List<HistoricalRental> history) {
        this.history = history;
        delegate.clear();
        delegate.addResources(history.stream().map(CarInfo::new).collect(Collectors.toSet()));
        history.forEach(hr -> delegate.assign(new CarInfo(hr), new HistoricalRentalAdapter(hr)));
        fireTableStructureChanged();
        listener.dataChanged();
    }


    @Override
    public int getRowCount() {
        return history.size();
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
        HistoricalRental event = history.get(rowIndex);
        if (columnIndex == 0) return event.getRegistration();
        if (columnIndex == 1) return event.getModel();
        if (columnIndex == 2) return event.getClientName();
        if (columnIndex == 3) return event.getClientEmail();
        if (columnIndex == 4) return event.getStart();
        if (columnIndex == 5) return event.getEnd();
        if (columnIndex == 6) return Duration.between(event.getStart(), event.getEnd());
        throw new IllegalArgumentException("Unknown column index : " + columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 4 || columnIndex == 5) return ZonedDateTime.class;
        if (columnIndex == 6) return Duration.class;
        return super.getColumnClass(columnIndex);
    }

    @Override
    public List<CarInfo> getResources() {
        return delegate.getResources();
    }

    @Override
    public Collection<HistoricalRentalAdapter> getEventsAssignedTo(CarInfo carInfo) {
        return delegate.getEventsAssignedTo(carInfo);
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

class CarInfo implements Resource {

    final String registration;
    final String model;

    public CarInfo(HistoricalRental historicalRental) {
        this.registration = historicalRental.getRegistration();
        this.model = historicalRental.getModel();
    }

    @Override
    public int hashCode() {
        return registration.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return ((CarInfo) obj).registration.equals(this.registration);
    }
}

class HistoricalRentalAdapter implements Task {
    final HistoricalRental historicalRental;

    HistoricalRentalAdapter(HistoricalRental historicalRental) {
        this.historicalRental = historicalRental;
    }

    @Override
    public ZonedDateTime getStart() {
        return historicalRental.getStart();
    }

    @Override
    public ZonedDateTime getEnd() {
        return historicalRental.getEnd();
    }
}
