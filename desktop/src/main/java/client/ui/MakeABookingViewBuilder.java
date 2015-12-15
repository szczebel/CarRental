package client.ui;

import client.ui.util.BackgroundOperation;
import client.ui.util.IntervalEditor;
import common.domain.Car;
import common.domain.Client;
import common.domain.RentalClass;
import common.service.AvailabilityService;
import common.service.BookingService;
import common.service.ClientService;
import common.util.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.components.table.TablePanel;

import javax.swing.*;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static client.ui.util.GuiHelper.datePicker;
import static client.ui.util.GuiHelper.rentalClassChooser;
import static swingutils.components.ComponentFactory.button;
import static swingutils.components.ComponentFactory.withTitledBorder;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;
import static swingutils.layout.forms.FormLayoutBuilders.simpleForm;

@Component
public class MakeABookingViewBuilder {

    @Autowired    AvailabilityService availabilityService;
    @Autowired    BookingService bookingService;
    @Autowired    ClientService clientService;
    @Autowired    RentalClasses rentalClasses;
    @Autowired    ClientListViewBuilder clientListViewBuilder;

    public JComponent build() {
        Cars cars = new Cars();
        AvailabilityQueryEditor availabilityQueryEditor = new AvailabilityQueryEditor(rentalClasses);
        refresh(availabilityQueryEditor::getQuery, cars::setData);
        TablePanel<Car> table = cars.createTable();
        table.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return borderLayout()
                .north(
                        flowLayout(
                                button("Search...", () -> {
                                            JOptionPane.showMessageDialog(null, availabilityQueryEditor.createComponent(), "Search for available cars...", JOptionPane.PLAIN_MESSAGE);
                                            refresh(availabilityQueryEditor::getQuery, cars::setData);
                                        }
                                ),
                                button("Refresh", () -> refresh(availabilityQueryEditor::getQuery, cars::setData)),
                                button("Book selected...", () -> bookClicked(table.getScrollPane(), table.getSelection(), availabilityQueryEditor::getQuery, cars::setData)),
                                table.getToolbar()
                        ))
                .center(table.getScrollPane())
                .build();
    }

    private void bookClicked(JComponent parent, Car selection, Supplier<AvailabilityService.BookingQuery> classChooser, Consumer<Collection<Car>> dataReceiver) {
        if (selection == null) {
            JOptionPane.showMessageDialog(parent, "Select a car to book in the table");
        } else {
            BackgroundOperation.execute(
                    clientService::fetchAll,
                    clients -> showBookDialog(parent, selection, classChooser, dataReceiver)
            );
        }
    }

    private void showBookDialog(JComponent parent, Car carToBook, Supplier<AvailabilityService.BookingQuery> queryProvider, Consumer<Collection<Car>> dataReceiver) {

        TablePanel<Client> table = clientListViewBuilder.build();
        int option = JOptionPane.showConfirmDialog(
                parent,
                withTitledBorder(table.getComponent(), "Select client to book for"),
                "Rent " + carToBook,
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Client selected = table.getSelection();
            if (selected != null) {
                BackgroundOperation.execute(
                        () -> bookingService.book(carToBook, selected, queryProvider.get().getInterval()),
                        () -> refresh(queryProvider, dataReceiver)
                );
            }
        }
    }

    private void refresh(Supplier<AvailabilityService.BookingQuery> queryProvider, Consumer<Collection<Car>> resultConsumer) {
        BackgroundOperation.execute(
                () -> availabilityService.findAvailableToBook(queryProvider.get()),
                resultConsumer
        );
    }

    static class AvailabilityQueryEditor extends IntervalEditor {

        JComboBox<RentalClass> classChooser;

        public AvailabilityQueryEditor(RentalClasses rentalClasses) {
            super(new Interval(ZonedDateTime.now(), ZonedDateTime.now().plusDays(7)));
            classChooser = rentalClassChooser(rentalClasses);
        }

        @Override
        public JComponent createComponent() {
            return simpleForm()
                    .addRow("From:", datePicker(from))
                    .addRow("To:",   datePicker(to))
                    .addRow("Class:", classChooser)
                    .build();
        }

        AvailabilityService.BookingQuery getQuery() {
            RentalClass selectedItem = (RentalClass) classChooser.getSelectedItem();
            return new AvailabilityService.BookingQuery(selectedItem, getInterval());
        }
    }
}
