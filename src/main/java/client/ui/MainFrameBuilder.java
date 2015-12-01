package client.ui;

import common.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;

@Component
public class MainFrameBuilder {

    @Autowired
    TestService testService;
    @Autowired
    FleetViewBuilder fleetViewBuilder;
    @Autowired
    ClientListViewBuilder clientListViewBuilder;
    @Autowired
    AvailableCarsViewBuilder availableCarsViewBuilder;
    @Autowired
    CurrentRentalsViewBuilder currentRentalsViewBuilder;
    @Autowired
    HistoricalRentalsViewBuilder historicalRentalsViewBuilder;

    @PostConstruct
    public void buildAndShow() {
        final JFrame frame = new JFrame("Car Rental");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane(SwingConstants.LEFT);
        tabs.addTab("Available cars", availableCarsViewBuilder.build());
        tabs.addTab("Current rentals", currentRentalsViewBuilder.build());
        tabs.addTab("Rental history", historicalRentalsViewBuilder.build());
        tabs.addTab("Fleet", fleetViewBuilder.build());
        tabs.addTab("Clients", clientListViewBuilder.build());
        tabs.addTab("Other", createOther());

        frame.add(tabs);


        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JComponent createOther() {
        JPanel panel = new JPanel();
        panel.add(new JButton(new AbstractAction("Test connection") {
            public void actionPerformed(ActionEvent e) {
                BackgroundOperation.execute(
                        testService::getServerInfo,
                        result -> JOptionPane.showMessageDialog(panel, "Server returned IP: " + result),
                        exception -> JOptionPane.showMessageDialog(panel, "Connection failed")
                );
            }
        }));
        return panel;
    }
}
