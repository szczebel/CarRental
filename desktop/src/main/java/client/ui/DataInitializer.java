package client.ui;

import common.service.ClientService;
import common.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.background.BackgroundOperation;

import javax.annotation.PostConstruct;

@Component
public class DataInitializer {

    @Autowired Customers customers;
    @Autowired ClientService clientService;
    @Autowired FleetCache fleetCache;
    @Autowired FleetService fleetService;

    @PostConstruct
    void initialize() {
        BackgroundOperation.execute(
                clientService::fetchAll,
                customers::setData
        );
        BackgroundOperation.execute(
                fleetService::fetchAll,
                fleetCache::setData
        );
    }
}
