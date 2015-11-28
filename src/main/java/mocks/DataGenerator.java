package mocks;

import common.domain.Car;
import common.domain.Client;
import org.springframework.beans.factory.InitializingBean;

import java.util.Random;

public class DataGenerator implements InitializingBean {

    MockFleetService fleetService;
    MockClientService clientService;
    MockRentalService rentalService;
    private final Random random = new Random();


    @Override
    public void afterPropertiesSet() throws Exception {
        generateFleet();
        generateClients();
        generateRentalData();
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

    private void generateRentalData() {
        int fleetSize = fleetService.fleet.size();
        for (int i = 0; i < fleetSize / 2; ++i) {
            try {
                rentalService.rent(randomCar(), randomClient());
            } catch (IllegalArgumentException e) {
                System.out.println("");
                //do nothing, we can skip some rentals in generated data
            }
        }
    }

    private Client randomClient() {
        return clientService.clients.get(random.nextInt(clientService.clients.size()));
    }


    private Car randomCar() {
        return fleetService.fleet.get(random.nextInt(fleetService.fleet.size()));
    }

    public void setFleetService(MockFleetService fleetService) {
        this.fleetService = fleetService;
    }

    public void setClientService(MockClientService clientService) {
        this.clientService = clientService;
    }

    public void setRentalService(MockRentalService rentalService) {
        this.rentalService = rentalService;
    }
}
