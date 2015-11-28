package common.service;


import common.domain.HistoricalRental;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Predicate;

public interface HistoryService {

    List<HistoricalRental> fetchHistory(Query query);

    class Query implements Predicate<HistoricalRental> {
        final ZonedDateTime start;
        final ZonedDateTime end;

        public Query(ZonedDateTime start, ZonedDateTime end) {
            this.start = start;
            this.end = end;
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
