package server.service;

import common.domain.Client;
import common.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.entity.PersistentClient;
import server.publish.ChangePublisher;
import server.repositories.PersistentClientDao;

import java.util.ArrayList;
import java.util.List;

@Component("clientService")
public class ClientServiceImpl implements ClientService {

    @Autowired PersistentClientDao dao;
    @Autowired ChangePublisher changePublisher;

    @Override
    public List<Client> fetchAll() {
        ArrayList<Client> clients = new ArrayList<>();
        dao.findAll().forEach(c -> clients.add(c.toClient()));
        return clients;
    }

    @Override
    @Transactional
    public void create(Client client) {
        if(dao.exists(client.getEmail())) throw new IllegalArgumentException("Customer with this email already exists");
        dao.save(new PersistentClient(client));
        changePublisher.publishNewClient(client);
    }

}
