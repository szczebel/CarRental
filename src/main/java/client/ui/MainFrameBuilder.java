package client.ui;

import common.service.TestService;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MainFrameBuilder {

    private TestService testService;

    private FleetViewBuilder fleetViewBuilder;
    private ClientListViewBuilder clientListViewBuilder;
    private AvailableCarsViewBuilder availableCarsViewBuilder;
    private CurrentRentalsViewBuilder currentRentalsViewBuilder;

    @SuppressWarnings("unused")
    public void buildAndShow() {
        final JFrame frame = new JFrame("Car Rental");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane(SwingConstants.LEFT);
        tabs.addTab("Available cars", availableCarsViewBuilder.build());
        tabs.addTab("Current rentals", currentRentalsViewBuilder.build());
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

    @SuppressWarnings("unused")
    public void setFleetViewBuilder(FleetViewBuilder fleetViewBuilder) {
        this.fleetViewBuilder = fleetViewBuilder;
    }

    @SuppressWarnings("unused")
    public void setClientListViewBuilder(ClientListViewBuilder clientListViewBuilder) {
        this.clientListViewBuilder = clientListViewBuilder;
    }

    @SuppressWarnings("unused")
    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    @SuppressWarnings("unused")
    public void setAvailableCarsViewBuilder(AvailableCarsViewBuilder availableCarsViewBuilder) {
        this.availableCarsViewBuilder = availableCarsViewBuilder;
    }

    public void setCurrentRentalsViewBuilder(CurrentRentalsViewBuilder currentRentalsViewBuilder) {
        this.currentRentalsViewBuilder = currentRentalsViewBuilder;
    }
}
