package client;


import config.RemoteServices;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import javax.swing.*;

@ComponentScan(basePackages = "client.ui")
@Import(RemoteServices.class)
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnnotationConfigApplicationContext(GUI.class));
    }
}
