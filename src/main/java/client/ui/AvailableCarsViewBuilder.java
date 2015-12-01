package client.ui;

import common.domain.Car;
import common.domain.Client;
import common.service.AvailabilityService;
import common.service.ClientService;
import common.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

@Component
public class AvailableCarsViewBuilder {

    @Autowired
    AvailabilityService availabilityService;
    @Autowired
    RentalService rentalService;
    @Autowired
    ClientService clientService;

    public JComponent build() {

        CarsTableModel tableModel = new CarsTableModel();

        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table));
        panel.add(buildToolbar(tableModel, table), BorderLayout.NORTH);
        refresh(tableModel);

        return panel;
    }

    private JComponent buildToolbar(CarsTableModel tableModel, JTable table) {
        JPanel panel = new JPanel();

        //todo refactor out Util.createButton(lambda)
        panel.add(new JButton(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh(tableModel);
            }
        }));

        panel.add(new JButton(new AbstractAction("Rent...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(panel, "Select a car to rent in the table");
                } else {
                    BackgroundOperation.execute(
                            clientService::fetchAll,
                            clients -> showRentDialog(panel, clients, tableModel.getCarAt(table.convertRowIndexToModel(selectedRow)), tableModel)
                    );
                }

            }
        }));


        return panel;
    }

    private void showRentDialog(JComponent parent, List<Client> clients, Car carToRent, CarsTableModel tableModel) {

        ClientListTableModel clientsModel = new ClientListTableModel();
        clientsModel.setData(clients);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Select client to rent to"));
        JTable clientsTable = new JTable(clientsModel);
        panel.add(new JScrollPane(clientsTable));
        int option = JOptionPane.showConfirmDialog(parent, panel, "Rent " + carToRent, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Client client = clientsModel.getClientAt(clientsTable.convertRowIndexToModel(selectedRow));
                BackgroundOperation.execute(
                        () -> rentalService.rent(carToRent, client),
                        () -> refresh(tableModel)
                );
            }
        }
    }

    private void refresh(CarsTableModel tableModel) {
        BackgroundOperation.execute(
                availabilityService::findAvailableCars,
                tableModel::setData
        );
    }
}
