package client;


import datageneration.DataGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import javax.swing.*;

@ComponentScan(basePackages = "client.ui")
@ImportResource("classpath:mockServicesContext.xml")
@Import(DataGenerator.class)
public class GUIWithMocks {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnnotationConfigApplicationContext(GUIWithMocks.class));
    }
}
