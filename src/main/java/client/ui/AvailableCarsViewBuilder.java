package client.ui;

import common.service.AvailabilityService;
import common.service.RentalService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AvailableCarsViewBuilder {

    private AvailabilityService availabilityService;
    private RentalService rentalService;

    public JComponent build() {

        CarsTableModel tableModel = new CarsTableModel();

        JPanel panel = new JPanel(new BorderLayout());
        JComponent table = new JTable(tableModel);
        panel.add(new JScrollPane(table));
        panel.add(buildToolbar(tableModel), BorderLayout.NORTH);

        return panel;
    }

    private JComponent buildToolbar(CarsTableModel tableModel) {
        JPanel panel = new JPanel();

        panel.add(new JButton(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                BackgroundOperation.execute(
                        availabilityService::findAvailableCars,
                        tableModel::setData
                );
            }
        }));

        panel.add(new JButton(new AbstractAction("Rent...") {
            @Override
            public void actionPerformed(ActionEvent e) {

                //todo implement me
            }
        }));


        return panel;
    }

    public void setAvailabilityService(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }


    public void setRentalService(RentalService rentalService) {
        this.rentalService = rentalService;
    }
}
