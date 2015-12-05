package common.domain;

import java.time.DayOfWeek;
import java.util.Arrays;
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
        final DataPoint hoursSummary;
        final DataPoint profitSummary;

        Map<DayOfWeek, DataPoint> dayOfWeekHoursAverages = new HashMap<>();
        Map<DayOfWeek, DataPoint> dayOfWeekProfitAverages = new HashMap<>();

        public Statistics(List<RentalClass> classes) {
            hoursSummary = new DataPoint(classes);
            profitSummary = new DataPoint(classes);
            Arrays.asList(DayOfWeek.values()).forEach(
                    dow -> {
                        dayOfWeekHoursAverages.put(dow, new DataPoint(classes));
                        dayOfWeekProfitAverages.put(dow, new DataPoint(classes));
                    }
            );
        }

        public DataPoint getHoursSummary() {
            return hoursSummary;
        }

        public DataPoint getProfitSummary() {
            return profitSummary;
        }

        public Map<DayOfWeek, DataPoint> getDayOfWeekHoursAverages() {
            return dayOfWeekHoursAverages;
        }

        public Map<DayOfWeek, DataPoint> getDayOfWeekProfitAverages() {
            return dayOfWeekProfitAverages;
        }
    }

    public static class DataPoint {
        double overall;
        Map<String, Double> perClass = new HashMap<>();


        public DataPoint(List<RentalClass> classes) {
            classes.forEach(c -> {
                perClass.put(c.getName(), 0d);
            });
        }

        public double getOverall() {
            return overall;
        }

        public Map<String, Double> getPerClass() {
            return perClass;
        }
    }
}
