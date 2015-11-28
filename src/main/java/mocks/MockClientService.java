package mocks;

import common.domain.Client;
import common.service.ClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MockClientService implements ClientService {
    List<Client> clients = new ArrayList<>();

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
