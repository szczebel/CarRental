package mocks;

import common.domain.Client;
import common.service.ClientService;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class MockClientService implements ClientService, InitializingBean {
    List<Client> clients = new ArrayList<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        //generate some data
        String[] names = {"John", "Thomas", "Christopher", "Wesley", "Lucas", "Gregory", "Rhonda", "Leticia", "Jane", "Courtney", "Kathy", "Angela", "David", "Brett", "Michael", "Sean", "Ross", "Monica", "Chandler"};
        String[] surnames = {"Suzuki", "White", "Fonda", "Griffin", "Nistor", "Washington", "Rainman", "Butterfly", "Zappa", "Johnson", "Beckham", "Dean", "Fowler", "Beck", "Petty", "Brinkworth", "Nasdac", "Williams", "Cox", "Arquette", "Greene", "Geller", "Bink", "Tribbiani", "Clunky", "Wright", "Bentley", "Coppola", "Pitt", "Jolie", "Padaki"};
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            String name = names[random.nextInt(names.length)];
            String surname = surnames[random.nextInt(surnames.length)];
            create(new Client(name + " " + surname, name + "." + surname + "." + random.nextInt(10000) + "@gmail.com"));
        }
    }

    @Override
    public List<Client> fetchAll() {
        return new ArrayList<>(clients);
    }

    @Override
    public void create(Client client) {
        if (clients.stream().anyMatch(email(client)))
            throw new IllegalArgumentException("Client already exists: " + client);
        clients.add(client);
    }

    private Predicate<Client> email(Client newClient) {
        return client -> client.getEmail().equals(newClient.getEmail());
    }
}
