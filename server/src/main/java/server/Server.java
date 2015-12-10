package server;


import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext(
                "/dataGenerationContext.xml", //comment out if not needing data generation
                "/persistence-h2.xml",
//                "/persistence-mysql.xml",
                "/mainServerContext.xml"
        );
        LoggerFactory.getLogger("Server").info("=========================================== server startup completed ===========================================");
    }

}
