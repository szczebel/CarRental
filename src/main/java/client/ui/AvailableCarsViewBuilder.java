package client.ui;

import common.domain.Car;
import common.domain.Client;
import common.service.AvailabilityService;
import common.service.ClientService;
import common.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.List;

import static client.ui.GuiHelper.*;

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
        refresh(tableModel);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return borderLayout()
                .north(
                        toolbar(
                                button("Refresh", () -> refresh(tableModel)),
                                button("Rent...", () -> rentClicked(table, tableModel))
                        ))
                .center(inScrollPane(table))
                .get();
    }

    private void rentClicked(JTable table, CarsTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(table, "Select a car to rent in the table");
        } else {
            BackgroundOperation.execute(
                    clientService::fetchAll,
                    clients -> showRentDialog(table, clients, tableModel.getCarAt(table.convertRowIndexToModel(selectedRow)), tableModel)
            );
        }
    }

    private void showRentDialog(JComponent parent, List<Client> clients, Car carToRent, CarsTableModel tableModel) {

        ClientListTableModel clientsModel = new ClientListTableModel();
        clientsModel.setData(clients);
        JTable clientsTable = new JTable(clientsModel);
        int option = JOptionPane.showConfirmDialog(
                parent,
                withTitledBorder(inScrollPane(clientsTable), "Select client to rent to"),
                "Rent " + carToRent,
                JOptionPane.OK_CANCEL_OPTION);

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
