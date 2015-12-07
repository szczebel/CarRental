package mocks;

import common.domain.Car;
import common.domain.Client;
import common.domain.CurrentRental;
import common.domain.RentalClass;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Component
public class DataGenerator {

    @Autowired
    MockFleetService fleetService;
    @Autowired
    MockRentalClassService rentalClassService;
    @Autowired
    MockClientService clientService;
    @Autowired
    MockRentalService rentalService;
    @Autowired
    MockBookingService bookingService;


    private final Random random = new Random();

    @SuppressWarnings("unused")
    @PostConstruct
    public void generate() throws Exception {
        System.out.println("Generating data...");
        generateClasses();
        generateFleet();
        generateClients();
        generateBetterRentalData();
    }

    private void generateClasses() {
        rentalClassService.create(new RentalClass("Economy", 5));
        rentalClassService.create(new RentalClass("Intermediate", 6));
        rentalClassService.create(new RentalClass("Fullsize", 7));
        rentalClassService.create(new RentalClass("SUV", 8));
        rentalClassService.create(new RentalClass("Elite", 10));
    }

    private void generateFleet() {
        String[] models = {"Ford Mondeo", "Fiat Multipla", "Lexus", "Mercedes S", "Peugeot 307", "Renault Safrane", "Mazda 6", "Volvo XC60"};
        List<RentalClass> rentalClasses = rentalClassService.fetchAll();
        Map<String, RentalClass> classPerModel = new HashMap<>();
        Arrays.asList(models).forEach(s -> classPerModel.put(s, rentalClasses.get(random.nextInt(rentalClasses.size()))));
        for (int i = 100; i <= 150; i++) {
            String model = models[random.nextInt(models.length)];
            fleetService.create(
                    new Car(
                            model,
                            1000 + random.nextInt(9000) + "KHZ" + i,
                            classPerModel.get(model)
                    )
            );
        }
    }

    private void generateClients() {
        String[] names = {"John", "Thomas", "Christopher", "Wesley", "Lucas", "Gregory", "Rhonda", "Leticia", "Jane", "Courtney", "Kathy", "Angela", "David", "Brett", "Michael", "Sean", "Ross", "Monica", "Chandler"};
        String[] surnames = {"Suzuki", "White", "Fonda", "Griffin", "Nistor", "Washington", "Rainman", "Butterfly", "Zappa", "Johnson", "Beckham", "Dean", "Fowler", "Beck", "Petty", "Brinkworth", "Nasdac", "Williams", "Cox", "Arquette", "Greene", "Geller", "Bink", "Tribbiani", "Clunky", "Wright", "Bentley", "Coppola", "Pitt", "Jolie", "Padaki"};
        for (int i = 0; i < 200; i++) {
            String name = names[random.nextInt(names.length)];
            String surname = surnames[random.nextInt(surnames.length)];
            clientService.create(new Client(name + " " + surname, name + "." + surname + "." + random.nextInt(10000) + "@gmail.com"));
        }
    }

    private void generateBetterRentalData() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime from = now.minusDays(60);
        ZonedDateTime to = now.plusDays(60);
        fleetService.fetchAll().forEach(car -> buildLineOfWork(car, from, to));
    }

    private void buildLineOfWork(Car car, ZonedDateTime start, ZonedDateTime end) {
        ZonedDateTime now = ZonedDateTime.now();

        ZonedDateTime currentTime = start;
        CurrentRental currentRental = null;
        while (currentTime.isBefore(now)) {
            if (currentRental != null) rentalService.returnCar(car.getRegistration());
            currentTime = fastForward(currentTime);
            ZonedDateTime plannedEnd = currentTime.plusHours(12 + random.nextInt(144)).plusMinutes(random.nextInt(60));
            if (currentTime.isBefore(now)) currentRental = rentalService.rent(car, randomClient(), plannedEnd);
            currentTime = fastForwardTo(plannedEnd);
        }
        while (currentTime.isBefore(end)) {
            currentTime = fastForward(currentTime);
            ZonedDateTime bookingStart = currentTime;
            currentTime = fastForward(currentTime);
            ZonedDateTime bookingEnd = currentTime;
            bookingService.book(car, randomClient(), new Interval(bookingStart, bookingEnd));
        }
        rentalService.clock = MockRentalService.SystemClock;
    }


    private ZonedDateTime fastForward(ZonedDateTime currentTime) {
        currentTime = currentTime.plusHours(12 + random.nextInt(144)).plusMinutes(random.nextInt(60));
        rentalService.clock = Clock.fixed(Instant.from(currentTime), ZoneId.systemDefault());
        return currentTime;
    }

    private ZonedDateTime fastForwardTo(ZonedDateTime newTime) {
        rentalService.clock = Clock.fixed(Instant.from(newTime), ZoneId.systemDefault());
        return newTime;
    }


    private Client randomClient() {
        return clientService.clients.get(random.nextInt(clientService.clients.size()));
    }
}
