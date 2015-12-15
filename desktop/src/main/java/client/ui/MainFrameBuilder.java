package client.ui;

import client.ui.booking.BookingsViewBuilder;
import client.ui.history.HistoricalRentalsViewBuilder;
import client.ui.scheduleview.ScheduleViewBuilder;
import client.ui.util.BackgroundOperation;
import common.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.Colors;
import swingutils.components.GradientPanel;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

import static swingutils.components.ComponentFactory.flatButton;
import static swingutils.layout.LayoutBuilders.flowLayout;
import static swingutils.layout.LayoutBuilders.wrapInPanel;
import static swingutils.layout.cards.CardSwitcherFactory.MenuPlacement.LEFT;
import static swingutils.layout.cards.CardSwitcherFactory.cardLayoutBuilder;
import static swingutils.layout.cards.MenuItemFunctions.create;

@Component
public class MainFrameBuilder {

    @Autowired FleetCache fleetCache;
    @Autowired TestService testService;
    @Autowired FleetViewBuilder fleetViewBuilder;
    @Autowired RentalClassViewBuilder rentalClassViewBuilder;
    @Autowired ClientListViewBuilder clientListViewBuilder;
    @Autowired MakeARentViewBuilder makeARentViewBuilder;
    @Autowired MakeABookingViewBuilder makeABookingViewBuilder;
    @Autowired CurrentRentalsViewBuilder currentRentalsViewBuilder;
    @Autowired BookingsViewBuilder bookingsViewBuilder;
    @Autowired HistoricalRentalsViewBuilder historicalRentalsViewBuilder;
    @Autowired ScheduleViewBuilder scheduleViewBuilder;


    @SuppressWarnings("unused")
    @PostConstruct
    void startup() {
        //todo show splash, initialize cache(s) in the backgorund, and build UI
        fleetCache.reload(r -> buildAndShow());
//        buildAndShow();
    }

    void buildAndShow() {
        installLAF();
        final JFrame frame = new JFrame("Car Rental");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(createContent(frame));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    JComponent createContent(JFrame frame) {
        return cardLayoutBuilder(LEFT, create(this::button, JComponent::setOpaque), menuPanel())
                .addTab("Available to rent",    makeARentViewBuilder.build())
                .addTab("Current rentals",      currentRentalsViewBuilder.build())
                .addTab("Available to book",    makeABookingViewBuilder.build())
                .addTab("Bookings",             bookingsViewBuilder.build())
                .addTab("Rental history",       historicalRentalsViewBuilder.build())
                .addTab("Fleet",                fleetViewBuilder.build())
                .addTab("Rental class",         rentalClassViewBuilder.build())
                .addTab("Clients",              clientListViewBuilder.build().getComponent())
                .addTab("Schedule",             scheduleViewBuilder.build())
                .addTab("Other", createOther(frame))
                .build();
    }

    private JComponent button(String label, Runnable action) {
        JComponent panel = wrapInPanel(flatButton(label, action));
        panel.setBackground(Colors.niceOrange);
        panel.setOpaque(false);
        return panel;
    }

    private JPanel menuPanel() {
        return new GradientPanel(Color.white, Color.lightGray, true);
    }

    private void installLAF() {
        try {

            UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private JComponent createOther(java.awt.Component parent) {
        return flowLayout(
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
