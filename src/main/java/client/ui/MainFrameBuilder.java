package client.ui;

import client.ui.util.BackgroundOperation;
import common.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;

import static client.ui.util.GuiHelper.*;

@Component
public class MainFrameBuilder {

    @Autowired TestService testService;
    @Autowired FleetViewBuilder fleetViewBuilder;
    @Autowired RentalClassViewBuilder rentalClassViewBuilder;
    @Autowired ClientListViewBuilder clientListViewBuilder;
    @Autowired AvailableCarsViewBuilder availableCarsViewBuilder;
    @Autowired CurrentRentalsViewBuilder currentRentalsViewBuilder;
    @Autowired HistoricalRentalsViewBuilder historicalRentalsViewBuilder;

    @SuppressWarnings("unused")
    @PostConstruct
    public void buildAndShow() {
        installLAF();
        final JFrame frame = new JFrame("Car Rental");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(createContent(frame));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    JComponent createContent(JFrame frame) {
        return tabbedPane(SwingConstants.LEFT)
                .addTab("Available cars",   availableCarsViewBuilder.build())
                .addTab("Current rentals",  currentRentalsViewBuilder.build())
                .addTab("Rental history",   historicalRentalsViewBuilder.build())
                .addTab("Fleet",            fleetViewBuilder.build())
                .addTab("Rental class",     rentalClassViewBuilder.build())
                .addTab("Clients",          clientListViewBuilder.build())
                .addTab("Other",            createOther(frame))
                .build();
    }

    private void installLAF() {
        try {

            UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private JComponent createOther(java.awt.Component parent) {
        return toolbar(
                button(
                        "Test connection",
                        () -> BackgroundOperation.execute(
                                testService::getServerInfo,
                                result -> JOptionPane.showMessageDialog(parent, "Server returned IP: " + result),
                                exception -> JOptionPane.showMessageDialog(parent, "Connection failed")
                        )
                )
        );
    }
}
