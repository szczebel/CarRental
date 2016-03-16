package server.publish;

import common.domain.Car;
import common.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

@Component
public class ChangePublisher {

    @Autowired JmsMessagingTemplate messagingTemplate;
    @Autowired Destination newClientTopic;
    @Autowired Destination newCarTopic;

    public void publishNewClient(Client newClient){
        messagingTemplate.convertAndSend(newClientTopic, newClient);
    }

    public void publishNewCar(Car newCar){
        messagingTemplate.convertAndSend(newCarTopic, newCar);
    }
}
