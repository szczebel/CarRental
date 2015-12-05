package common.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalHistory {

    final List<HistoricalRental> records;
    final Statistics statistics;

    public RentalHistory(List<HistoricalRental> records, Statistics statistics) {
        this.records = records;
        this.statistics = statistics;
    }

    public List<HistoricalRental> getRecords() {
        return records;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public static class Statistics {
        DataPoint summary;
        Map<LocalDate, DataPoint> series;
        Map<DayOfWeek, DataPoint> dailyAverages;
    }

    public static class DataPoint {
        double rentHoursTotal;
        Map<String, Double> rentHoursPerClass = new HashMap<>();

        double rentProfitTotal;
        Map<String, Double> rentProfitPerClass = new HashMap<>();
    }
}
