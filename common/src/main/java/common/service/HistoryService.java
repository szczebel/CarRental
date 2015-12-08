package common.service;


import common.domain.HistoricalRental;
import common.domain.RentalHistory;
import common.util.Interval;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.function.Predicate;

public interface HistoryService {

    RentalHistory fetchHistory(Query query);

    class Query implements Predicate<HistoricalRental>, Serializable {
        final Interval interval;

        public Query(Interval interval) {
            this.interval = interval;
        }

        public ZonedDateTime getStart() {
            return interval.from();
        }

        public ZonedDateTime getEnd() {
            return interval.to();
        }

        @Override
        public boolean test(HistoricalRental historicalRental) {
            return interval.overlaps(historicalRental.getInterval());
        }
    }
}
