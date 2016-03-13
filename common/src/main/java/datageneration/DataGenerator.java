package datageneration;

import common.domain.Car;
import common.domain.Client;
import common.domain.CurrentRental;
import common.domain.RentalClass;
import common.service.*;
import common.util.FreezableClock;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.*;

@Component
public class DataGenerator {

    @Autowired
    FleetService fleetService;
    @Autowired
    RentalClassService rentalClassService;
    @Autowired
    ClientService clientService;
    @Autowired
    RentalService rentalService;
    @Autowired
    BookingService bookingService;
    @Autowired
    FreezableClock freezableClock;


    private final Random random = new Random();
    private List<Client> clients;

    @SuppressWarnings("unused")
    @PostConstruct
    public void generate() throws Exception {
        long now = System.currentTimeMillis();
        System.out.println("Generating data...");
        //generate(100, 200, 60, 60);
        generate(5, 20, 20, 20);
        System.out.println("Data generated in " + (System.currentTimeMillis() - now) + " ms" );
    }

    private void generate(int fleetSize, int customerBaseSize, int daysOfHistory, int daysOfBookings) {
        generateClasses();
        generateFleet(fleetSize);
        generateClients(customerBaseSize);
        generateRentalData(daysOfHistory, daysOfBookings);
    }

    protected void generateClasses() {
        rentalClassService.create(new RentalClass("Economy", 5));
        rentalClassService.create(new RentalClass("Intermediate", 6));
        rentalClassService.create(new RentalClass("Fullsize", 7));
        rentalClassService.create(new RentalClass("SUV", 8));
        rentalClassService.create(new RentalClass("Elite", 10));
    }

    protected void generateFleet(int howMany) {
        String[] models = {"Ford Mondeo", "Fiat Multipla", "Lexus", "Mercedes S", "Peugeot 307", "Renault Safrane", "Mazda 6", "Volvo XC60"};
        List<RentalClass> rentalClasses = rentalClassService.fetchAll();
        Map<String, RentalClass> classPerModel = new HashMap<>();
        Arrays.asList(models).forEach(s -> classPerModel.put(s, rentalClasses.get(random.nextInt(rentalClasses.size()))));
        for (int i = 100; i <= 100+howMany; i++) {
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

    protected void generateClients(int howMany) {
        String[] names = {"John", "Thomas", "Christopher", "Wesley", "Lucas", "Gregory", "Rhonda", "Leticia", "Jane", "Courtney", "Kathy", "Angela", "David", "Brett", "Michael", "Sean", "Ross", "Monica", "Chandler"};
        String[] surnames = {"Suzuki", "White", "Fonda", "Griffin", "Nistor", "Washington", "Rainman", "Butterfly", "Zappa", "Johnson", "Beckham", "Dean", "Fowler", "Beck", "Petty", "Brinkworth", "Nasdac", "Williams", "Cox", "Arquette", "Greene", "Geller", "Bink", "Tribbiani", "Clunky", "Wright", "Bentley", "Coppola", "Pitt", "Jolie", "Padaki"};
        for (int i = 0; i < howMany; i++) {
            String name = names[random.nextInt(names.length)];
            String surname = surnames[random.nextInt(surnames.length)];
            clientService.create(new Client(name + " " + surname, name + "." + surname + "." + random.nextInt(10000) + "@gmail.com"));
        }
        clients = clientService.fetchAll();
    }

    protected void generateRentalData(int daysOfHistory, int daysOfBookings) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime from = now.minusDays(daysOfHistory);
        ZonedDateTime to = now.plusDays(daysOfBookings);
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
        freezableClock.unfreze();
    }


    private ZonedDateTime fastForward(ZonedDateTime currentTime) {
        currentTime = currentTime.plusHours(12 + random.nextInt(144)).plusMinutes(random.nextInt(60));
        freezableClock.freezeTime(currentTime);
        return currentTime;
    }

    private ZonedDateTime fastForwardTo(ZonedDateTime time) {
        freezableClock.freezeTime(time);
        return time;
    }


    private Client randomClient() {
        return clients.get(random.nextInt(clients.size()));
    }
}
