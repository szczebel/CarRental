package server.service;

import common.domain.Client;
import common.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import server.entity.PersistentClient;
import server.repositories.PersistentClientDao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component("clientService")
public class ClientServiceImpl implements ClientService {

    @Autowired PersistentClientDao dao;

    @Override
    public List<Client> fetchAll() {
        ArrayList<Client> clients = new ArrayList<>();
        dao.findAll().forEach(c -> clients.add(c.toClient()));
        return clients;
    }

    @Override
    public void create(Client client) {
        dao.save(new PersistentClient(client));
    }

    private Predicate<Client> email(Client newClient) {
        return client -> client.getEmail().equals(newClient.getEmail());
    }
}
