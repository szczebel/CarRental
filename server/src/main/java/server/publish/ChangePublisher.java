package server.publish;

import common.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

@Component
public class ChangePublisher {

    @Autowired JmsMessagingTemplate messagingTemplate;
    @Autowired Destination newClientChannel;

    public void publishNewClient(Client newClient){
        messagingTemplate.convertAndSend(newClientChannel, newClient);
    }
}
