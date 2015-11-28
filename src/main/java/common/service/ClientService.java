package common.service;

import common.domain.Client;

import java.util.List;

public interface ClientService {
    List<Client> fetchAll();

    void create(Client client);
}
