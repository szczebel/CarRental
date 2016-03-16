package client;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

public class PubSub {

    public static final String NEW_CLIENT_TOPIC = "NEW_CLIENT_TOPIC";
    public static final String NEW_CAR_TOPIC = "NEW_CAR_TOPIC";

    @Value("${jmsUrl:tcp://localhost:8089}")
    String jmsUrl;

    @Bean
    JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
        ActiveMQConnectionFactory f = new ActiveMQConnectionFactory(jmsUrl);
        f.setTrustAllPackages(true);
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);
        factory.setConnectionFactory(f);
        factory.setConcurrency("1");
        return factory;
    }

}
