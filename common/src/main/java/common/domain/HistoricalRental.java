package common.domain;

import common.util.Interval;

import java.time.ZonedDateTime;

public class HistoricalRental extends AbstractAssignment {

    public HistoricalRental(CurrentRental currentRental, ZonedDateTime end) {
        super(currentRental.getCar(), currentRental.getClient(), new Interval(currentRental.getStart(), end));
    }

}
