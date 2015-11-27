package server;


import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("/mainServerContext.xml");
    }

}
