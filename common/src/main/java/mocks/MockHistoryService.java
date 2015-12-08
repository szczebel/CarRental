package mocks;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import common.domain.HistoricalRental;
import common.domain.RentalClass;
import common.domain.RentalHistory;
import common.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static common.util.TimeUtils.toMidnight;

@Component
public class MockHistoryService implements HistoryService {

    @Autowired
    MockRentalClassService rentalClassService;
    @Autowired
    MockFleetService fleetService;

    private List<HistoricalRental> records = new ArrayList<>();

    void saveEvent(HistoricalRental event) {
        records.add(event);
    }

    @Override
    public RentalHistory fetchHistory(Query query) {
        List<HistoricalRental> filtered = records.stream().filter(query).collect(Collectors.toList());
        return new RentalHistory(filtered, calculateStatistics(filtered, query));
    }

    private RentalHistory.Statistics calculateStatistics(List<HistoricalRental> rentals, Query query) {
        List<RentalClass> rentalClasses = rentalClassService.fetchAll();
        RentalHistory.Statistics statistics = calculate(rentals, rentalClasses, query.getStart(), query.getEnd());

        //dow breakdown
        Map<DayOfWeek, Multimap<String, Double>> hoursCounter = create();
        ZonedDateTime dayToConsider = toMidnight(query.getStart());
        while (dayToConsider.isBefore(query.getEnd())) {
            DayOfWeek dayOfWeek = dayToConsider.getDayOfWeek();
            RentalHistory.ValuePerClass hoursPerClass = getHoursPerClass(rentals, rentalClasses, dayToConsider, dayToConsider.plusDays(1));
            hoursPerClass.keySet().forEach(rc -> hoursCounter.get(dayOfWeek).put(rc, hoursPerClass.getValueFor(rc)));
            dayToConsider = dayToConsider.plusDays(1);
        }

        statistics.getUtilizationPerDayOfWeek().forEach((dow, utilizationPerClass) -> {
            Multimap<String, Double> perDOW = hoursCounter.get(dow);
            perDOW.keySet().forEach(rc -> {
                Collection<Double> series = perDOW.get(rc);
                double howManyOfThisWeekDay = series.size();
                double sumOfHoursOnThisDOWInThisClass = sum(series);
                double utilization = (sumOfHoursOnThisDOWInThisClass / howManyOfThisWeekDay) / fleetService.countOf(rc);
                utilizationPerClass.setValueFor(rc, utilization);
            });
        });

        return statistics;
    }

    private double sum(Collection<Double> series) {
        double sum = 0;
        for (Double v : series) {
            sum += v;
        }
        return sum;
    }

    private RentalHistory.Statistics calculate(List<HistoricalRental> rentals, List<RentalClass> rentalClasses, ZonedDateTime from, ZonedDateTime to) {
        double overallEarnings = 0;
        double overallHours = 0;
        RentalHistory.ValuePerClass earningsPerClass = new RentalHistory.ValuePerClass(rentalClasses);
        RentalHistory.ValuePerClass hoursPerClass = new RentalHistory.ValuePerClass(rentalClasses);
        for (HistoricalRental rental : rentals) {
            double hours = countHours(from, to, rental);
            double earnings = hours * rental.getHourlyRate();
            overallHours += hours;
            overallEarnings += earnings;

            earningsPerClass.setValueFor(rental.getRentalClassName(), earnings + earningsPerClass.getValueFor(rental.getRentalClassName()));
            hoursPerClass.setValueFor(rental.getRentalClassName(), hours + hoursPerClass.getValueFor(rental.getRentalClassName()));
        }
        double queryDurationInDays = (double) Duration.between(from, to).toDays();
        double overallUtilization = (overallHours / queryDurationInDays) / (double) fleetService.fleetSize();
        RentalHistory.Statistics statistics = new RentalHistory.Statistics(rentalClasses, overallEarnings, overallUtilization);
        rentalClasses.forEach(rentalClass -> {
            String rcn = rentalClass.getName();
            statistics.getEarningsPerClass().setValueFor(rcn, earningsPerClass.getValueFor(rcn));
            statistics.getUtilizationPerClass().setValueFor(rcn, (hoursPerClass.getValueFor(rcn) / queryDurationInDays) / (double) fleetService.countOf(rcn));
        });
        return statistics;
    }

    private RentalHistory.ValuePerClass getHoursPerClass(List<HistoricalRental> rentals, List<RentalClass> rentalClasses, ZonedDateTime from, ZonedDateTime to) {
        RentalHistory.ValuePerClass hoursPerClass = new RentalHistory.ValuePerClass(rentalClasses);
        for (HistoricalRental rental : rentals) {
            double hours = countHours(from, to, rental);
            hoursPerClass.setValueFor(rental.getRentalClassName(), hours + hoursPerClass.getValueFor(rental.getRentalClassName()));
        }
        return hoursPerClass;
    }

    private double countHours(ZonedDateTime from, ZonedDateTime to, HistoricalRental rental) {
        ZonedDateTime intervalStart = from.isAfter(rental.getStart()) ? from : rental.getStart();
        ZonedDateTime intervalEnd = to.isBefore(rental.getEnd()) ? to : rental.getEnd();
        if (intervalStart.isBefore(intervalEnd)) return Duration.between(intervalStart, intervalEnd).toHours();
        else return 0;
    }

    private Map<DayOfWeek, Multimap<String, Double>> create() {
        Map<DayOfWeek, Multimap<String, Double>> averages = new HashMap<>();
        Arrays.asList(DayOfWeek.values()).forEach(dow -> averages.put(dow, HashMultimap.create()));
        return averages;
    }
}
