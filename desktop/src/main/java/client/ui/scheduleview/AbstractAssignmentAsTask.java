package client.ui.scheduleview;

import common.domain.AbstractAssignment;
import common.domain.Booking;
import common.domain.CurrentRental;
import common.domain.HistoricalRental;
import schedule.model.Task;

import java.time.ZonedDateTime;

public class AbstractAssignmentAsTask implements Task {
    private final AbstractAssignment abstractAssignment;
    private final Type type;

    public AbstractAssignmentAsTask(HistoricalRental hr) {
        abstractAssignment = hr;
        type = Type.HISTORICAL;
    }

    public AbstractAssignmentAsTask(CurrentRental hr) {
        abstractAssignment = hr;
        type = Type.CURRENT;
    }

    public AbstractAssignmentAsTask(Booking hr) {
        abstractAssignment = hr;
        type = Type.BOOKING;
    }

    @Override
    public ZonedDateTime getStart() {
        return abstractAssignment.getStart();
    }

    @Override
    public ZonedDateTime getEnd() {
        return abstractAssignment.getEnd();
    }

    public Type getType() {
        return type;
    }

    public AbstractAssignment getAbstractAssignment() {
        return abstractAssignment;
    }

    public enum Type {
        HISTORICAL, CURRENT, BOOKING
    }
}
