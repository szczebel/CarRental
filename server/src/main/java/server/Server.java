package server;


import datageneration.DataGenerator;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Import(DataGenerator.class) //comment out if not needing data generation
@ImportResource({"/persistence-h2.xml", "/mainServerContext.xml"})
//@ImportResource({"/persistence-mysql.xml", "/mainServerContext.xml"})
public class Server {

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(Server.class);
        LoggerFactory.getLogger("Server").info("=========================================== server startup completed ===========================================");
    }
}
