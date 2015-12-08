package server;


import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext(
                "/dataGenerationContext.xml" //comment out if not needing data generation
                ,"/mainServerContext.xml"
        );
    }

}
