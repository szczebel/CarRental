package client.ui;

import common.domain.Car;
import common.domain.Client;
import common.domain.RentalClass;
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

    @Autowired AvailabilityService availabilityService;
    @Autowired RentalService rentalService;
    @Autowired ClientService clientService;
    @Autowired RentalClasses rentalClasses;

    public JComponent build() {

        FleetTableModel tableModel = new FleetTableModel();
        JComboBox<RentalClass> classChooser = new JComboBox<>(rentalClasses.getComboBoxModel());
        refresh(tableModel, (RentalClass) classChooser.getSelectedItem());
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        classChooser.addItemListener(e -> refresh(tableModel, (RentalClass) classChooser.getSelectedItem()));

        return borderLayout()
                .north(
                        toolbar(
                                label("Rental class:"),
                                classChooser,
                                button("Rent...", () -> rentClicked(table, tableModel, classChooser))
                        ))
                .center(inScrollPane(table))
                .build();
    }

    private void rentClicked(JTable table, FleetTableModel tableModel, JComboBox<RentalClass> classChooser) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(table, "Select a car to rent in the table");
        } else {
            BackgroundOperation.execute(
                    clientService::fetchAll,
                    clients -> showRentDialog(table, clients, tableModel.getCarAt(table.convertRowIndexToModel(selectedRow)), tableModel, classChooser)
            );
        }
    }

    private void showRentDialog(JComponent parent, List<Client> clients, Car carToRent, FleetTableModel tableModel, JComboBox<RentalClass> classChooser) {

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
                        () -> refresh(tableModel, (RentalClass) classChooser.getSelectedItem())
                );
            }
        }
    }

    private void refresh(FleetTableModel tableModel, RentalClass selectedItem) {
        BackgroundOperation.execute(
                () -> availabilityService.findAvailableCars(selectedItem),
                tableModel::setData
        );
    }
}
