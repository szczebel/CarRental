package server;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.Destination;

public class JmsConfig {

    @Bean
    Object jmsBroker() throws Exception {
        return BrokerFactory.createBroker("broker:(tcp://localhost:8089)?persistent=false&useJmx=true", true);
    }

    @DependsOn("jmsBroker")
    @Bean
    JmsMessagingTemplate messagingTemplate() {
        ActiveMQConnectionFactory f = new ActiveMQConnectionFactory("tcp://localhost:8089");
        f.setTrustAllPackages(true);
        JmsMessagingTemplate t = new JmsMessagingTemplate(f);
        t.afterPropertiesSet();
        return t;
    }

    @Bean
    Destination newClientTopic() {
        return new ActiveMQTopic("NEW_CLIENT_TOPIC");
    }

    @Bean
    Destination newCarTopic() {
        return new ActiveMQTopic("NEW_CAR_TOPIC");
    }

}
