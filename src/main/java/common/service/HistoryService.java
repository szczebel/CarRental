package common.service;


import common.domain.HistoricalRental;

import java.time.ZonedDateTime;
import java.util.List;

public interface HistoryService {

    List<HistoricalRental> fetchHistory(Query query);

    class Query {
        final ZonedDateTime start;
        final ZonedDateTime end;

        public Query(ZonedDateTime start, ZonedDateTime end) {
            this.start = start;
            this.end = end;
        }
    }
}
