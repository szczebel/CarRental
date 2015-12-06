package client.ui;

import client.ui.util.BackgroundOperation;
import common.domain.Car;
import common.domain.RentalClass;
import common.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

import static client.ui.util.GuiHelper.*;

@Component
public class FleetViewBuilder {

    @Autowired FleetService fleetService;
    @Autowired RentalClasses rentalClasses;

    public JComponent build() {

        FleetTableModel tableModel = new FleetTableModel();
        refresh(tableModel);
        JTable table = new JTable(tableModel);

        return borderLayout()
                .north(
                        toolbar(
                                button("Refresh", () -> refresh(tableModel)),
                                button("Add...", () -> addCarClicked(table, tableModel))
                        ))
                .center(inScrollPane(table))
                .build();
    }

    private void addCarClicked(JComponent panel, FleetTableModel tableModel) {

        JTextField registration = new JTextField();
        JTextField model = new JTextField();
        JComboBox<RentalClass> classChooser = new JComboBox<>(rentalClasses.getComboBoxModel());
        JComponent createDialogContent = borderLayout()
                .north(registration)
                .center(model)
                .south(classChooser)
                .build();

        JOptionPane.showMessageDialog(panel, createDialogContent);
        BackgroundOperation.execute(
                () -> fleetService.create(new Car(model.getText(), registration.getText(), (RentalClass) classChooser.getSelectedItem())),
                () -> refresh(tableModel)
        );
    }

    private void refresh(FleetTableModel tableModel) {
        BackgroundOperation.execute(
                fleetService::fetchAll,
                tableModel::setData
        );
    }

}
