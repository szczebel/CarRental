package common.domain;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.*;

public class RentalHistory implements Serializable {

    final List<HistoricalRental> records;
    final Statistics statistics;

    public RentalHistory(){this(null, null);}

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

    public static class Statistics implements Serializable {
        final double overallEarnings;
        final double overallUtilization;
        final ValuePerClass earningsPerClass;
        final ValuePerClass utilizationPerClass;

        Map<DayOfWeek, ValuePerClass> utilizationPerDayOfWeek = new HashMap<>();

        public Statistics(List<RentalClass> classes, double overallEarnings, double overallUtilization) {
            this.overallEarnings = overallEarnings;
            this.overallUtilization = overallUtilization;
            earningsPerClass = new ValuePerClass(classes);
            utilizationPerClass = new ValuePerClass(classes);
            Arrays.asList(DayOfWeek.values()).forEach(
                    dow -> utilizationPerDayOfWeek.put(dow, new ValuePerClass(classes))
            );
        }

        public ValuePerClass getEarningsPerClass() {
            return earningsPerClass;
        }

        public ValuePerClass getUtilizationPerClass() {
            return utilizationPerClass;
        }

        public double getOverallEarnings() {
            return overallEarnings;
        }

        public double getOverallUtilization() {
            return overallUtilization;
        }

        public Map<DayOfWeek, ValuePerClass> getUtilizationPerDayOfWeek() {
            return utilizationPerDayOfWeek;
        }
    }

    public static class ValuePerClass implements Serializable{
        Map<String, Double> perClass = new HashMap<>();

        public ValuePerClass(List<RentalClass> classes) {
            classes.forEach(c -> perClass.put(c.getName(), 0d));
        }

        public void setValueFor(String rentalClassName, double value) {
            perClass.put(rentalClassName, value);
        }

        public double getValueFor(String rentalClassName) {
            return perClass.get(rentalClassName);
        }

        public Set<String> keySet() {
            return perClass.keySet();
        }
    }
}
