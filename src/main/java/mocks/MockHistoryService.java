package mocks;

import common.domain.HistoricalRental;
import common.service.HistoryService;

import java.util.ArrayList;
import java.util.List;

public class MockHistoryService implements HistoryService {

    private List<HistoricalRental> records = new ArrayList<>();

    void saveEvent(HistoricalRental event) {
        records.add(event);
    }

    @Override
    public List<HistoricalRental> fetchHistory(Query query) {
        return new ArrayList<>(records);
    }
}
