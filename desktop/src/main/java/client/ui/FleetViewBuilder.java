package client.ui;

import client.ui.util.BackgroundOperation;
import common.domain.Car;
import common.domain.RentalClass;
import common.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.components.table.TablePanel;

import javax.swing.*;

import static client.ui.util.GuiHelper.rentalClassChooser;
import static swingutils.components.ComponentFactory.button;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;
import static swingutils.layout.forms.FormLayoutBuilders.simpleForm;

@Component
public class FleetViewBuilder {

    @Autowired FleetService fleetService;
    @Autowired FleetCache fleetCache;
    @Autowired RentalClasses rentalClasses;

    public JComponent build() {

        Cars cars = new Cars();
        cars.setData(fleetCache.getFleet());
        TablePanel<Car> table = cars.createTable();

        return borderLayout()
                .north(
                        flowLayout(
                                button("Refresh", () -> fleetCache.reload(cars::setData)),
                                button("Add...", () -> addCarClicked(table.getComponent(), cars))
                        ))
                .center(table.getComponent())
                .build();
    }

    private void addCarClicked(JComponent panel, Cars tableModel) {

        JTextField registration = new JTextField();
        JTextField model = new JTextField();
        JComboBox<RentalClass> classChooser = rentalClassChooser(rentalClasses);
        JComponent dialogContent = simpleForm()
                .addRow("Reg#:", registration)
                .addRow("Model:", model)
                .addRow("Class:", classChooser)
                .build();


        JOptionPane.showMessageDialog(panel.getParent(), dialogContent);
        BackgroundOperation.execute(
                () -> fleetService.create(new Car(model.getText(), registration.getText(), (RentalClass) classChooser.getSelectedItem())),
                () -> fleetCache.reload(tableModel::setData)
        );
    }

}
