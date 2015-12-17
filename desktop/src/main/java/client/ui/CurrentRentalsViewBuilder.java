package client.ui;

import common.domain.CurrentRental;
import common.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.background.BackgroundOperation;
import swingutils.components.progress.BusyFactory;
import swingutils.components.progress.ProgressIndicatingComponent;
import swingutils.components.progress.ProgressIndicator;
import swingutils.components.table.TablePanel;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

import static swingutils.components.ComponentFactory.button;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;

@Component
public class CurrentRentalsViewBuilder {

    @Autowired RentalService rentalService;

    public JComponent build() {
        CurrentRentals currentRentals = new CurrentRentals();
        TablePanel<CurrentRental> table = currentRentals.createTable();

        ProgressIndicatingComponent pi = BusyFactory.lockAndWhirlWhenBusy();
        pi.setContent(borderLayout()
                .north(
                        flowLayout(
                                button("Refresh", () -> refresh(currentRentals::setData, pi)),
                                button("Return selected", () -> returnClicked(table.getScrollPane(), table.getSelection(), currentRentals::setData, pi)),
                                table.getToolbar()
                        ))
                .center(table.getScrollPane())
                .build());
        refresh(currentRentals::setData, pi);
        return pi.getComponent();
    }


    private void returnClicked(JComponent parent, CurrentRental selection, Consumer<List<CurrentRental>> setData, ProgressIndicator pi) {
        if (selection != null) {
            BackgroundOperation.execute(
                    () -> rentalService.returnCar(selection.getRegistration()),
                    () -> refresh(setData, pi),
                    pi
            );
        } else {
            JOptionPane.showMessageDialog(parent, "Please select a car being returned");
        }
    }

    private void refresh(Consumer<List<CurrentRental>> consumer, ProgressIndicator pi) {
        BackgroundOperation.execute(
                rentalService::getCurrentRentals,
                consumer,
                pi
        );
    }
}
