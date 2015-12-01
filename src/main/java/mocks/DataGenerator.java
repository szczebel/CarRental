package mocks;

import common.domain.Car;
import common.domain.Client;
import common.domain.CurrentRental;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

@Component
public class DataGenerator {

    @Autowired
    MockFleetService fleetService;
    @Autowired
    MockClientService clientService;
    @Autowired
    MockRentalService rentalService;
    private final Random random = new Random();

    @SuppressWarnings("unused")
    @PostConstruct
    public void generate() throws Exception {
        System.out.println("Generating data...");
        generateFleet();
        generateClients();
        generateBetterRentalData();
    }

    private void generateFleet() {
        String[] models = {"Ford Mondeo", "Fiat Multipla", "Lexus", "Mercedes S", "Peugeot 307", "Renault Safrane", "Mazda 6", "Volvo XC60"};
        for (int i = 100; i <= 150; i++) {
            fleetService.create(new Car(models[random.nextInt(models.length)], 1000 + random.nextInt(9000) + "KHZ" + i));
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
        ZonedDateTime weekAgo = now.minusDays(60);
        fleetService.fetchAll().forEach(car -> buildLineOfWork(car, weekAgo, now));
    }

    private void buildLineOfWork(Car car, ZonedDateTime start, ZonedDateTime end) {
        ZonedDateTime currentTime = start;
        CurrentRental currentRental = null;
        while (currentTime.isBefore(end)) {
            if (currentRental != null) rentalService.returnCar(car.getRegistration());
            currentTime = fastForward(currentTime);
            if (currentTime.isBefore(end)) currentRental = rentalService.rent(car, randomClient());
            currentTime = fastForward(currentTime);
        }
        rentalService.clock = MockRentalService.SystemClock;

    }

    private ZonedDateTime fastForward(ZonedDateTime currentTime) {
        currentTime = currentTime.plusHours(12 + random.nextInt(144)).plusMinutes(random.nextInt(60));
        rentalService.clock = Clock.fixed(Instant.from(currentTime), ZoneId.systemDefault());
        return currentTime;
    }


    private Client randomClient() {
        return clientService.clients.get(random.nextInt(clientService.clients.size()));
    }
}
