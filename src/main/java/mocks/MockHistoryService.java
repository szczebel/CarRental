package mocks;

import common.domain.HistoricalRental;
import common.domain.RentalHistory;
import common.service.HistoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MockHistoryService implements HistoryService {

    private List<HistoricalRental> records = new ArrayList<>();

    void saveEvent(HistoricalRental event) {
        records.add(event);
    }

    @Override
    public RentalHistory fetchHistory(Query query) {
        List<HistoricalRental> filtered = records.stream().filter(query).collect(Collectors.toList());
        return new RentalHistory(filtered, calculateStatistics(filtered));
    }

    private RentalHistory.Statistics calculateStatistics(List<HistoricalRental> rentals) {
        return new RentalHistory.Statistics();
    }
}
