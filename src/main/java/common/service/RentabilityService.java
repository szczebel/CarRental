package common.service;

import common.domain.Car;
import common.domain.RentalClass;

import java.time.ZonedDateTime;
import java.util.List;

public interface RentabilityService {
    List<Car> findAvailableCars(Query quaey);

    class Query {
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
