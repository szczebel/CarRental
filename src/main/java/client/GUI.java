package client;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;

public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClassPathXmlApplicationContext("/mainClientContext.xml"));
    }
}
