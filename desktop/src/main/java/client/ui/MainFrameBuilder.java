package client.ui;

import client.ui.booking.BookingsViewBuilder;
import client.ui.history.HistoricalRentalsViewBuilder;
import client.ui.scheduleview.ScheduleViewBuilder;
import common.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.Colors;
import swingutils.background.BackgroundOperation;
import swingutils.components.GradientPanel;
import swingutils.components.progress.BusyFactory;
import swingutils.components.progress.ProgressIndicatingComponent;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

import static swingutils.components.ComponentFactory.*;
import static swingutils.layout.LayoutBuilders.flowLayout;
import static swingutils.layout.LayoutBuilders.wrapInPanel;
import static swingutils.layout.cards.CardSwitcherFactory.MenuPlacement.LEFT;
import static swingutils.layout.cards.CardSwitcherFactory.cardLayout;
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
        return cardLayout(LEFT, create(this::menuButton, JComponent::setOpaque), menu -> new GradientPanel(Color.white, Color.lightGray, true, decorate(menu).withEmptyBorder(4, 0, 4, 0).get()))
                .addTab("Available to rent", decorate(makeARentViewBuilder.build()).withGradientHeader("Cars available to rent at the moment").withEmptyBorder(4, 4, 4, 4).get())
                .addTab("Current rentals",      decorate(currentRentalsViewBuilder.build()   ).withGradientHeader("Cars currently rented").withEmptyBorder(4,4,4,4).get())
                .addTab("Available to book",    decorate(makeABookingViewBuilder.build()     ).withGradientHeader("Cars available to book").withEmptyBorder(4,4,4,4).get())
                .addTab("Bookings",             decorate(bookingsViewBuilder.build()         ).withGradientHeader("Bookings").withEmptyBorder(4,4,4,4).get())
                .addTab("Rental history",       decorate(historicalRentalsViewBuilder.build()).withGradientHeader("History of rentals").withEmptyBorder(4,4,4,4).get())
                .addTab("Fleet",                decorate(fleetViewBuilder.build()            ).withGradientHeader("My fleet").withEmptyBorder(4,4,4,4).get())
                .addTab("Rental class",         decorate(rentalClassViewBuilder.build()      ).withGradientHeader("My rental classes").withEmptyBorder(4,4,4,4).get())
                .addTab("Customers",            decorate(clientListTab()                     ).withGradientHeader("My customers").withEmptyBorder(4,4,4,4).get())
                .addTab("Schedule",             decorate(scheduleViewBuilder.build()         ).withGradientHeader("Schedule chart for historical rentals, current rentals and bookings").withEmptyBorder(4,4,4,4).get())
                .addTab("Other",                decorate(createOther(frame)                  ).withGradientHeader("Other tools and settings").withEmptyBorder(4,4,4,4).get())
                .build();
    }

    private JComponent clientListTab() {
        ProgressIndicatingComponent pi = BusyFactory.lockAndWhirlWhenBusy();
        pi.setContent(clientListViewBuilder.build(pi).getComponent());
        return pi.getComponent();
    }

    private JComponent menuButton(String label, Runnable action) {
        JButton button = flatButton(label, action);
        button.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));
        JComponent panel = wrapInPanel(button);
        panel.setBackground(Colors.niceOrange);
        panel.setOpaque(false);
        return panel;
    }

    private void installLAF() {
        try {

            UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private JComponent createOther(java.awt.Component parent) {
        ProgressIndicatingComponent pi = BusyFactory.lockAndWhirlWhenBusy();
        JComponent contect = flowLayout(
                button(
                        "Test connection",
                        () -> BackgroundOperation.execute(
                                testService::getServerInfo,
                                result -> JOptionPane.showMessageDialog(parent, "Server returned IP: " + result),
                                exception -> JOptionPane.showMessageDialog(parent, "Connection failed"),
                                pi
                        )
                )
        );
        pi.setContent(contect);
        return pi.getComponent();
    }
}
