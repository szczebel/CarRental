package client;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;

public class Client {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClassPathXmlApplicationContext("/mainClientContext.xml");
            }
        });
    }
}
