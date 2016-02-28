package client.ui;

import client.ui.booking.BookingsViewBuilder;
import client.ui.history.HistoricalRentalsViewBuilder;
import client.ui.scheduleview.ScheduleViewBuilder;
import common.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.background.BackgroundOperation;
import swingutils.components.GradientPanel;
import swingutils.components.progress.BusyFactory;
import swingutils.components.progress.ProgressIndicatingComponent;
import swingutils.layout.cards.MenuItems;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

import static swingutils.components.ComponentFactory.button;
import static swingutils.components.ComponentFactory.decorate;
import static swingutils.layout.LayoutBuilders.flowLayout;
import static swingutils.layout.cards.CardSwitcherFactory.MenuPlacement.LEFT;
import static swingutils.layout.cards.CardSwitcherFactory.cardLayout;

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
        return cardLayout(LEFT, MenuItems.NakedOrange, mainMenuCustomizer())
                .addTab("Available to rent",    buildNiceTab(makeARentViewBuilder.build(),          "Cars available to rent at the moment"))
                .addTab("Current rentals",      buildNiceTab(currentRentalsViewBuilder.build(),     "Cars currently rented"))
                .addTab("Available to book",    buildNiceTab(makeABookingViewBuilder.build(),       "Cars available to book"))
                .addTab("Bookings",             buildNiceTab(bookingsViewBuilder.build(),           "Bookings"))
                .addTab("Rental history",       buildNiceTab(historicalRentalsViewBuilder.build(),  "History of rentals"))
                .addTab("Fleet",                buildNiceTab(fleetViewBuilder.build(),              "My fleet"))
                .addTab("Rental class",         buildNiceTab(rentalClassViewBuilder.build(),        "My rental classes"))
                .addTab("Customers",            buildNiceTab(clientListTab(),                       "My customers"))
                .addTab("Schedule",             buildNiceTab(scheduleViewBuilder.build(),           "Schedule chart for historical rentals, current rentals and bookings"))
                .addTab("Other",                buildNiceTab(createOther(frame),                    "Other tools and settings"))
                .build();
    }

    private Function<JComponent, JComponent> mainMenuCustomizer() {
        return menu -> new GradientPanel(Color.white, SystemColor.control, true, decorate(menu).withEmptyBorder(4, 0, 4, 0).get());
    }

    private JComponent buildNiceTab(JComponent toDecorate, String header) {
        return decorate(toDecorate).withGradientHeader(header).withEmptyBorder(4,4,4,4).get();
    }

    private JComponent clientListTab() {
        ProgressIndicatingComponent pi = BusyFactory.lockAndWhirlWhenBusy();
        pi.setContent(clientListViewBuilder.build(pi).getComponent());
        return pi.getComponent();
    }

    private void installLAF() {
        try {
            UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.PlasticLookAndFeel());
            UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());//this will fail on linux, so the above will be the default
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
                                result -> JOptionPane.showMessageDialog(parent, "Server said: " + result),
                                exception -> JOptionPane.showMessageDialog(parent, "Connection failed"),
                                pi
                        )
                )
        );
        pi.setContent(contect);
        return pi.getComponent();
    }
}
