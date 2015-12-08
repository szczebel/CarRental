package server;


import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext mocks = new ClassPathXmlApplicationContext(
                "/dataGenerationContext.xml",
                "/mockServicesContext.xml");

        new ClassPathXmlApplicationContext(new String[]{"/mainServerContext.xml"}, mocks);
    }

}
