package mocks;

import common.domain.HistoricalRental;
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
    public List<HistoricalRental> fetchHistory(Query query) {
        return records.stream().filter(query).collect(Collectors.toList());
    }
}
