package client.ui.fullscheduleview;

import client.ui.util.CarResource;
import client.ui.util.FleetCache;
import common.domain.Booking;
import common.domain.CurrentRental;
import common.domain.HistoricalRental;
import schedule.basic.BasicScheduleModel;
import schedule.model.ScheduleModel;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class FullScheduleModel implements ScheduleModel<CarResource, AbstractAssignmentAsTask> {

    final FleetCache fleetCache;
    private BasicScheduleModel<CarResource, AbstractAssignmentAsTask> delegate = new BasicScheduleModel<>();
    private Listener listener;

    FullScheduleModel(FleetCache fleetCache) {
        this.fleetCache = fleetCache;
        delegate.addResources(fleetCache.getFleet().stream().map(CarResource::new).collect(Collectors.toSet()));
    }

    void clear() {
        delegate.clearAllData();
        delegate.addResources(fleetCache.getFleet().stream().map(CarResource::new).collect(Collectors.toSet()));
    }

    void addHistory(Collection<HistoricalRental> history) {
        history.forEach(a -> delegate.assign(new CarResource(a), new AbstractAssignmentAsTask(a)));
        listener.dataChanged();
    }
    void addCurrent(List<CurrentRental> currentRentals) {
        currentRentals.forEach(a -> delegate.assign(new CarResource(a), new AbstractAssignmentAsTask(a)));
        listener.dataChanged();
    }
    void addBookings(Collection<Booking> bookings) {
        bookings.forEach(a -> delegate.assign(new CarResource(a), new AbstractAssignmentAsTask(a)));
        listener.dataChanged();
    }

    @Override
    public List<CarResource> getResources() {
        return delegate.getResources();
    }

    @Override
    public Collection<AbstractAssignmentAsTask> getEventsAssignedTo(CarResource carResource) {
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
}
