package client.ui;

import common.domain.Car;
import common.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Component
public class FleetViewBuilder {

    @Autowired
    FleetService fleetService;

    public JComponent build() {

        CarsTableModel tableModel = new CarsTableModel();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildToolbar(tableModel), BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(tableModel)));
        refresh(tableModel);

        return panel;
    }

    private JComponent buildToolbar(CarsTableModel tableModel) {
        JPanel panel = new JPanel();

        panel.add(new JButton(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh(tableModel);
            }
        }));

        panel.add(new JButton(new AbstractAction("Add...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String registration = JOptionPane.showInputDialog(panel, "Registration", "Add car to fleet", JOptionPane.QUESTION_MESSAGE);
                String model = JOptionPane.showInputDialog(panel, "Model", "Add car to fleet", JOptionPane.QUESTION_MESSAGE);
                BackgroundOperation.execute(
                        () -> fleetService.create(new Car(model, registration)),
                        () -> refresh(tableModel)
                );
            }
        }));


        return panel;
    }

    private void refresh(CarsTableModel tableModel) {
        BackgroundOperation.execute(
                fleetService::fetchAll,
                tableModel::setData
        );
    }

}
