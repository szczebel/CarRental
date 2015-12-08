package server.entity;

import common.domain.Client;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PersistentClient {

    @Id String email;
    @Column String name;

    protected PersistentClient(){}

    public PersistentClient(Client client) {
        email = client.getEmail();
        name = client.getName();
    }

    public Client toClient() {
        return new Client(name, email);
    }
}
