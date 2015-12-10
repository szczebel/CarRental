package client.ui.history;

import client.ui.scheduleview.AbstractAssignmentAsTask;
import common.domain.HistoricalRental;

class HistoricalRentalAsTask extends AbstractAssignmentAsTask {

    HistoricalRentalAsTask(HistoricalRental historicalRental) {
        super(historicalRental);
    }
}
