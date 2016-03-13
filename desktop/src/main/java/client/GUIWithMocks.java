package client;


import datageneration.DataGenerator;
import mocks.Mocks;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import javax.swing.*;

@Import({DataGenerator.class, Mocks.class})
@ComponentScan(basePackages = "client.ui")
public class GUIWithMocks {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnnotationConfigApplicationContext(GUIWithMocks.class));
    }
}
