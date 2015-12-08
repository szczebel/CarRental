package client.ui.booking;

import client.ui.fullscheduleview.AbstractAssignmentAsTask;
import common.domain.Booking;

class BookingAsTask extends AbstractAssignmentAsTask {

    BookingAsTask(Booking booking) {
        super(booking);
    }
}
