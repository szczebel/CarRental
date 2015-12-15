package client.ui;

import client.ui.util.BackgroundOperation;
import common.domain.Car;
import common.domain.Client;
import common.domain.RentalClass;
import common.service.AvailabilityService;
import common.service.ClientService;
import common.service.RentalService;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.EventListHolder;
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
        refresh(availabilityQueryEditor::getQuery, cars::setData);
        TablePanel<Car> table = cars.createTable();
        table.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return borderLayout()
                .north(
                        flowLayout(
                                button("Search...", () -> {
                                            JOptionPane.showMessageDialog(null, availabilityQueryEditor.getComponent(), "Search for available cars...", JOptionPane.PLAIN_MESSAGE);
                                            refresh(availabilityQueryEditor::getQuery, cars::setData);
                                        }
                                ),
                                button("Refresh", () -> refresh(availabilityQueryEditor::getQuery, cars::setData)),
                                button("Rent selected...", () -> rentClicked(table.getScrollPane(), table.getSelection(), cars, availabilityQueryEditor::getQuery)),
                                table.getToolbar()
                        ))
                .center(table.getScrollPane())
                .build();
    }

    private void rentClicked(JComponent parent, Car selection, EventListHolder<Car> cars, Supplier<AvailabilityService.RentQuery> queryProvider) {
        if (selection == null) {
            JOptionPane.showMessageDialog(null, "Select a car to rent in the table");
        } else {
            BackgroundOperation.execute(
                    clientService::fetchAll,
                    clients -> showRentDialog(parent, selection, queryProvider, cars::setData)
            );
        }
    }

    private void showRentDialog(JComponent parent, Car carToRent, Supplier<AvailabilityService.RentQuery> queryProvider, Consumer<Collection<Car>> dataReceiver) {

        TablePanel<Client> table = clientListViewBuilder.build();
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
                        () -> refresh(queryProvider, dataReceiver)
                );
            }
        }
    }

    private void refresh(Supplier<AvailabilityService.RentQuery> queryProvider, Consumer<Collection<Car>> dataReceiver) {
        BackgroundOperation.execute(
                () -> availabilityService.findAvailableToRent(queryProvider.get()),
                dataReceiver
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
