package client.ui.history;

import client.ui.FleetCache;
import client.ui.scheduleview.CarResource;
import common.domain.AbstractAssignment;
import common.domain.HistoricalRental;
import common.domain.RentalHistory;
import schedule.model.GenericScheduleModel;
import swingutils.EventListHolder;
import swingutils.components.table.TableFactory;
import swingutils.components.table.TablePanel;
import swingutils.components.table.descriptor.Columns;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

class HistoricalRentalsModel extends EventListHolder<HistoricalRental> {

    final FleetCache fleetCache;
    private GenericScheduleModel<CarResource, HistoricalRentalAsTask> delegate = new GenericScheduleModel<>();

    TablePanel<HistoricalRental> createTable() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy '@' HH:mm");
        Columns<HistoricalRental> columns = Columns.create(HistoricalRental.class)
                .column("Registration", String.class, AbstractAssignment::getRegistration)
                .column("Model", String.class, AbstractAssignment::getModel)
                .column("Customer name", String.class, AbstractAssignment::getClientName)
                .column("Customer email", String.class, AbstractAssignment::getClientEmail)
                .column("Start", String.class, cr -> cr.getStart().format(formatter))
                .column("End", String.class, cr -> cr.getEnd().format(formatter))
                .column("Duration", String.class, cr -> String.valueOf(cr.getDuration().toHours()) + " hours")
                ;
        return TableFactory.createTablePanel(getData(), columns);
    }

    HistoricalRentalsModel(FleetCache fleetCache) {
        this.fleetCache = fleetCache;
    }

    void setData(RentalHistory rentalHistory) {
        setData(rentalHistory.getRecords());
        delegate.clearAllData();
        delegate.addResources(fleetCache.getFleet().stream().map(CarResource::new).collect(Collectors.toSet()));
        delegate.assignAll(getData().stream().map(HistoricalRentalAsTask::new), t -> new CarResource(t.getAbstractAssignment()));
    }

    public GenericScheduleModel<CarResource, HistoricalRentalAsTask> asScheduleModel() {
        return delegate;
    }
}
