package client;


import config.RemoteServices;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

@ComponentScan(basePackages = "client.ui")
@Import({RemoteServices.class, PubSub.class})
@EnableJms
public class GUI {

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> new AnnotationConfigApplicationContext(GUI.class));
    }
}
