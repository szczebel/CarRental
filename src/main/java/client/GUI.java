package client;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;

public class GUI {

    public static void main(String[] args) {
        //ensuring data generation runs first
        ClassPathXmlApplicationContext mocks = new ClassPathXmlApplicationContext("/mockServicesContext.xml");
        //and now building GUI
        SwingUtilities.invokeLater(() -> new ClassPathXmlApplicationContext(new String[]{"/mainClientContext.xml"}, mocks));
    }
}
