package common.service;

import common.domain.Car;
import common.domain.RentalClass;
import common.util.Interval;

import java.util.List;

public interface BookabilityService {
    List<Car> findAvailableCars(Query quaey);

    class Query {
        final RentalClass rentalClass;
        final Interval interval;

        public Query(RentalClass rentalClass, Interval interval) {
            this.rentalClass = rentalClass;
            this.interval = interval;
        }

        public RentalClass getRentalClass() {
            return rentalClass;
        }

        public Interval getInterval() {
            return interval;
        }
    }
}
