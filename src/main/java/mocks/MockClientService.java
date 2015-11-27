package mocks;

import common.domain.Client;
import common.service.ClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockClientService implements ClientService{
    List<Client> clients = new ArrayList<>();


    {
        //generate some data
        String[] names = {"John", "Thomas", "Christopher", "Wesley", "Lucas", "Gregory", "Rhonda", "Leticia", "Jane", "Courtney", "Kathy", "Angela", "David", "Brett", "Michael", "Sean", "Ross", "Monica", "Chandler"};
        String[] surnames = {"Suzuki", "White", "Fonda", "Griffin", "Nistor", "Washington", "Rainman", "Butterfly", "Zappa", "Johnson", "Beckham", "Dean", "Fowler", "Beck", "Petty", "Brinkworth", "Nasdac", "Williams", "Cox", "Arquette", "Greene", "Geller", "Bink", "Tribbiani", "Clunky", "Wright", "Bentley", "Coppola", "Pitt", "Jolie", "Padaki"};
        Random random = new Random();
        for (int i = 0; i < 20 ; i++) {
            create(new Client(names[random.nextInt(names.length)] + " " + surnames[random.nextInt(surnames.length)],"+("+random.nextInt(90)+") "+(random.nextInt(900000000)+100000000)));
        }
    }

    @Override
    public List<Client> fetchAll() {
        return clients;
    }

    @Override
    public Client create(Client client) {
        clients.add(client);
        return client;
    }
}
