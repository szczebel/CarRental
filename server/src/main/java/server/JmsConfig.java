package server;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

public class JmsConfig {

    @Bean
    Object jmsBroker() throws Exception {
        return BrokerFactory.createBroker("broker:(tcp://localhost:8089)?persistent=false&useJmx=true", true);
    }

    @DependsOn("jmsBroker")
    @Bean
    ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory f = new ActiveMQConnectionFactory("tcp://localhost:8089");
        f.setTrustAllPackages(true);
        return f;
    }

    @Bean
    JmsMessagingTemplate messagingTemplate(ConnectionFactory connectionFactory) {
        JmsMessagingTemplate t = new JmsMessagingTemplate(connectionFactory);
        t.afterPropertiesSet();
        return t;
    }

    @Bean
    Destination newClientTopic() {
        return new ActiveMQTopic("NEW_CLIENT_CHANNEL");
    }

    //this will be on client side:
    @Bean
    JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1");
        return factory;
    }
}
