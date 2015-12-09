package common.service;

import common.domain.Car;
import common.domain.RentalClass;
import common.util.Interval;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;

public interface AvailabilityService {
    Collection<Car> findAvailableToRent(RentQuery query);

    Collection<Car> findAvailableToBook(BookingQuery query);

    class RentQuery implements Serializable {
        final RentalClass rentalClass;
        private final ZonedDateTime availableUntil;

        public RentQuery(RentalClass rentalClass, ZonedDateTime availableUntil) {
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

    class BookingQuery implements Serializable {
        final RentalClass rentalClass;
        final Interval interval;

        public BookingQuery(RentalClass rentalClass, Interval interval) {
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
