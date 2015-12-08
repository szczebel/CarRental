package common.service;

import common.domain.Car;
import common.domain.RentalClass;
import common.util.Interval;

import java.io.Serializable;
import java.util.Collection;

public interface BookabilityService {
    Collection<Car> findAvailableCars(Query quaey);

    class Query implements Serializable {
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
