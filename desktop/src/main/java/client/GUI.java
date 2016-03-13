package client;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import javax.swing.*;

@ComponentScan(basePackages = "client.ui")
@ImportResource("classpath:remoteServicesContext.xml")
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnnotationConfigApplicationContext(GUI.class));
    }
}
