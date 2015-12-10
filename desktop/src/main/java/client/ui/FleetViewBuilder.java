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
    @Autowired FleetCache fleetCache;
    @Autowired RentalClasses rentalClasses;

    public JComponent build() {

        FleetTableModel tableModel = new FleetTableModel();
        tableModel.setData(fleetCache.getFleet());
        JTable table = new JTable(tableModel);

        return borderLayout()
                .north(
                        toolbar(
                                button("Refresh", () -> fleetCache.reload(tableModel::setData)),
                                button("Add...", () -> addCarClicked(table, tableModel))
                        ))
                .center(inScrollPane(table))
                .build();
    }

    private void addCarClicked(JComponent panel, FleetTableModel tableModel) {

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
