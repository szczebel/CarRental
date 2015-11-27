package client.ui;

import common.domain.Car;
import common.service.FleetService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FleetViewBuilder {

    private FleetService fleetService;

    public JComponent build() {

        CarsTableModel tableModel = new CarsTableModel();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildToolbar(tableModel), BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(tableModel)));

        return panel;
    }

    private JComponent buildToolbar(CarsTableModel tableModel) {
        JPanel panel = new JPanel();

        panel.add(new JButton(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                BackgroundOperation.execute(
                        fleetService::fetchAll,
                        tableModel::setData
                );
            }
        }));

        panel.add(new JButton(new AbstractAction("Add...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String registration = JOptionPane.showInputDialog(panel, "Registration", "Add car to fleet", JOptionPane.QUESTION_MESSAGE);
                String model = JOptionPane.showInputDialog(panel, "Model", "Add car to fleet", JOptionPane.QUESTION_MESSAGE);
                BackgroundOperation.execute(
                        () -> fleetService.create(new Car(model, registration)),
                        tableModel::add
                );
            }
        }));


        return panel;
    }

    @SuppressWarnings("unused")
    public void setFleetService(FleetService fleetService) {
        this.fleetService = fleetService;
    }


}
