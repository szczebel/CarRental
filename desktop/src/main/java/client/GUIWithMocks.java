package client;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;

public class GUIWithMocks {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext mocks = new ClassPathXmlApplicationContext(
                "/dataGenerationContext.xml",
                "/mockServicesContext.xml");
        //and now building GUI
        SwingUtilities.invokeLater(() -> new ClassPathXmlApplicationContext(new String[]{"/mainClientContext.xml"}, mocks));
    }
}
