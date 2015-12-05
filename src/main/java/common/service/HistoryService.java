package common.service;


import common.domain.HistoricalRental;
import common.domain.RentalHistory;

import java.time.ZonedDateTime;
import java.util.function.Predicate;

public interface HistoryService {

    RentalHistory fetchHistory(Query query);

    class Query implements Predicate<HistoricalRental> {
        final ZonedDateTime start;
        final ZonedDateTime end;

        public Query(ZonedDateTime start, ZonedDateTime end) {
            this.start = start;
            this.end = end;
        }

        public ZonedDateTime getStart() {
            return start;
        }

        public ZonedDateTime getEnd() {
            return end;
        }

        @SuppressWarnings("RedundantIfStatement")
        @Override
        public boolean test(HistoricalRental historicalRental) {
            if (historicalRental.getEnd().isBefore(start)) return false;
            if (historicalRental.getStart().isAfter(end)) return false;
            return true;
        }
    }
}
