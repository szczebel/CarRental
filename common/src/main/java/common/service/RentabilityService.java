package common.service;

import common.domain.Car;
import common.domain.RentalClass;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;

public interface RentabilityService {
    Collection<Car> findAvailableCars(Query quaey);

    class Query implements Serializable {
        final RentalClass rentalClass;
        private final ZonedDateTime availableUntil;

        public Query(RentalClass rentalClass, ZonedDateTime availableUntil) {
            this.rentalClass = rentalClass;
            this.availableUntil = availableUntil;
        }

        public ZonedDateTime getAvailableUntil() {
            return availableUntil;
        }

        public RentalClass getRentalClass() {
            return rentalClass;
        }
    }
}
