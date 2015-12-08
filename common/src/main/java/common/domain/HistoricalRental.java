package common.domain;

import common.util.Interval;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class HistoricalRental extends AbstractAssignment implements Serializable {

    //for serialization
    public HistoricalRental() {
    }

    public HistoricalRental(CurrentRental currentRental, ZonedDateTime end) {
        super(currentRental.getCar(), currentRental.getClient(), new Interval(currentRental.getStart(), end));
    }

}
