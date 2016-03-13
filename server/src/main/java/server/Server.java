package server;


import common.util.FreezableClock;
import datageneration.DataGenerator;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;

@Import(DataGenerator.class) //comment out if not needing data generation
@ComponentScan({"server.multitenancy","server.service"})
@ImportResource({"/persistence-${persistencetype:h2}.xml", "/httpServerContext.xml"})
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
