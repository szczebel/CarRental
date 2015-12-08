package client;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;

public class GUI {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext remote = new ClassPathXmlApplicationContext(
                "/remoteServicesContext.xml");
        //and now building GUI
        SwingUtilities.invokeLater(() -> new ClassPathXmlApplicationContext(new String[]{"/mainClientContext.xml"}, remote));
    }
}
