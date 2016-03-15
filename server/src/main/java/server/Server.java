package server;


import common.util.FreezableClock;
import datageneration.DataGenerator;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;

@Import({DataGenerator.class, HttpConfig.class, JmsConfig.class})
@ComponentScan({"server.multitenancy","server.service","server.publish"})
@ImportResource("/persistence-${persistencetype:h2}.xml")
@EnableAspectJAutoProxy
public class Server {

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(Server.class);
        LoggerFactory.getLogger("Server").info("=========================================== server startup completed ===========================================");
    }

    @Bean
    FreezableClock currentTime() {
        return new FreezableClock();
    }
}
