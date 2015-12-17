package client.ui;

import common.domain.Car;
import common.domain.Client;
import common.domain.RentalClass;
import common.service.AvailabilityService;
import common.service.ClientService;
import common.service.RentalService;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.BackgroundOperation;
import swingutils.EventListHolder;
import swingutils.components.progress.BusyFactory;
import swingutils.components.progress.ProgressIndicatingComponent;
import swingutils.components.progress.ProgressIndicator;
import swingutils.components.table.TablePanel;

import javax.swing.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static client.ui.util.GuiHelper.datePicker;
import static client.ui.util.GuiHelper.rentalClassChooser;
import static common.util.TimeUtils.toMidnight;
import static swingutils.components.ComponentFactory.button;
import static swingutils.components.ComponentFactory.withTitledBorder;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;
import static swingutils.layout.forms.FormLayoutBuilders.simpleForm;

@Component
public class MakeARentViewBuilder {

    @Autowired    AvailabilityService availabilityService;
    @Autowired    RentalService rentalService;
    @Autowired    ClientService clientService;
    @Autowired    RentalClasses rentalClasses;
    @Autowired    ClientListViewBuilder clientListViewBuilder;

    public JComponent build() {

        Cars cars = new Cars();
        AvailabilityQueryEditor availabilityQueryEditor = new AvailabilityQueryEditor(rentalClasses);
        TablePanel<Car> table = cars.createTable();
        table.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ProgressIndicatingComponent pi = BusyFactory.lockAndWhirlWhenBusy();
        JComponent content = borderLayout()

                .north(
                        flowLayout(
                                button("Search...", () -> {
                                            JOptionPane.showMessageDialog(null, availabilityQueryEditor.getComponent(), "Search for available cars...", JOptionPane.PLAIN_MESSAGE);
                                            refresh(availabilityQueryEditor::getQuery, cars::setData, pi);
                                        }
                                ),
                                button("Refresh", () -> refresh(availabilityQueryEditor::getQuery, cars::setData, pi)),
                                button("Rent selected...", () -> rentClicked(table.getScrollPane(), table.getSelection(), cars, availabilityQueryEditor::getQuery, pi)),
                                table.getToolbar()
                        ))
                .center(table.getScrollPane())
                .build();
        pi.setContent(content);
        refresh(availabilityQueryEditor::getQuery, cars::setData, pi);
        return pi.getComponent();
    }

    private void rentClicked(JComponent parent, Car selection, EventListHolder<Car> cars, Supplier<AvailabilityService.RentQuery> queryProvider, ProgressIndicator pi) {
        if (selection == null) {
            JOptionPane.showMessageDialog(null, "Select a car to rent in the table");
        } else {
            BackgroundOperation.execute(
                    clientService::fetchAll,
                    clients -> showRentDialog(parent, selection, queryProvider, cars::setData, pi),
                    pi
            );
        }
    }

    private void showRentDialog(JComponent parent, Car carToRent, Supplier<AvailabilityService.RentQuery> queryProvider, Consumer<Collection<Car>> dataReceiver, ProgressIndicator pi) {

        TablePanel<Client> table = clientListViewBuilder.build(pi);
        int option = JOptionPane.showConfirmDialog(
                parent,
                withTitledBorder(table.getComponent(), "Select client to rent to"),
                "Rent " + carToRent,
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Client selection = table.getSelection();
            if (selection != null) {
                BackgroundOperation.execute(
                        () -> rentalService.rent(carToRent, selection, queryProvider.get().getAvailableUntil()),
                        () -> refresh(queryProvider, dataReceiver, pi)
                );
            }
        }
    }

    private void refresh(Supplier<AvailabilityService.RentQuery> queryProvider, Consumer<Collection<Car>> dataReceiver, ProgressIndicator pi) {
        BackgroundOperation.execute(
                () -> availabilityService.findAvailableToRent(queryProvider.get()),
                dataReceiver,
                pi
        );
    }

    static class AvailabilityQueryEditor {

        UtilDateModel until = new UtilDateModel(Date.from((ZonedDateTime.now().plusDays(7).toInstant())));
        JComboBox<RentalClass> classChooser;
        private JComponent component;

        public AvailabilityQueryEditor(RentalClasses rentalClasses) {
            classChooser = rentalClassChooser(rentalClasses);
            component = simpleForm()
                    .addRow("Rent until:", datePicker(until))
                    .addRow("Class:", classChooser)
                    .build();
        }

        AvailabilityService.RentQuery getQuery() {
            RentalClass selectedItem = (RentalClass) classChooser.getSelectedItem();
            return new AvailabilityService.RentQuery(selectedItem, toMidnight(ZonedDateTime.ofInstant(until.getValue().toInstant(), ZoneId.systemDefault())));
        }

        JComponent getComponent() {
            return component;
        }
    }

}
