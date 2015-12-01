package client.ui;

import common.domain.Car;
import common.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

import static client.ui.GuiHelper.*;

@Component
public class FleetViewBuilder {

    @Autowired FleetService fleetService;

    public JComponent build() {

        CarsTableModel tableModel = new CarsTableModel();
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

    private void addCarClicked(JComponent panel, CarsTableModel tableModel) {
        String registration = JOptionPane.showInputDialog(panel, "Registration", "Add car to fleet", JOptionPane.QUESTION_MESSAGE);
        String model = JOptionPane.showInputDialog(panel, "Model", "Add car to fleet", JOptionPane.QUESTION_MESSAGE);
        BackgroundOperation.execute(
                () -> fleetService.create(new Car(model, registration)),
                () -> refresh(tableModel)
        );
    }

    private void refresh(CarsTableModel tableModel) {
        BackgroundOperation.execute(
                fleetService::fetchAll,
                tableModel::setData
        );
    }

}
