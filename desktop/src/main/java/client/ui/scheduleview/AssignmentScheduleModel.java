package client.ui.scheduleview;

import client.ui.FleetCache;
import common.domain.Booking;
import common.domain.CurrentRental;
import common.domain.HistoricalRental;
import schedule.model.GenericScheduleModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentScheduleModel extends GenericScheduleModel<CarResource, AbstractAssignmentAsTask> {

    final FleetCache fleetCache;

    AssignmentScheduleModel(FleetCache fleetCache) {
        this.fleetCache = fleetCache;
    }

    void clear() {
        clearAllData();
        addResources(fleetCache.getFleet().stream().map(CarResource::new).collect(Collectors.toSet()));
    }

    void addHistory(Collection<HistoricalRental> history) {
        assignAll(history.stream().map(AbstractAssignmentAsTask::new), (a) -> new CarResource(a.getAbstractAssignment()));
    }

    void addCurrent(List<CurrentRental> currentRentals) {
        assignAll(currentRentals.stream().map(AbstractAssignmentAsTask::new), (a) -> new CarResource(a.getAbstractAssignment()));
    }

    void addBookings(Collection<Booking> bookings) {
        assignAll(bookings.stream().map(AbstractAssignmentAsTask::new), (a) -> new CarResource(a.getAbstractAssignment()));
    }
}
