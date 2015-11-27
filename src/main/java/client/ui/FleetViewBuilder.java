package client.ui;

import common.domain.Car;
import common.service.FleetService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class FleetViewBuilder {

    private FleetService fleetService;

    public JComponent build() {

        FleetTableModel tableModel = new FleetTableModel();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buildToolbar(tableModel), BorderLayout.NORTH);
        panel.add(new JScrollPane(buildTable(tableModel)));

        return panel;
    }

    private JComponent buildTable(FleetTableModel tableModel) {
        return new JTable(tableModel);
    }

    private JComponent buildToolbar(FleetTableModel tableModel) {
        JPanel panel = new JPanel();

        panel.add(new JButton(new AbstractAction("(Re)Load") {
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


    static class FleetTableModel extends AbstractTableModel {

        final static String[] COLUMN = {"Registration", "Model"};
        private List<Car> fleet = new ArrayList<>();

        void setData(List<Car> fleet) {
            this.fleet = fleet;
            fireTableStructureChanged();
        }

        void add(Car car) {
            fleet.add(car);
            fireTableStructureChanged();
        }


        @Override
        public int getRowCount() {
            return fleet.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Car car = fleet.get(rowIndex);
            if (columnIndex == 0) return car.getRegistration();
            if (columnIndex == 1) return car.getModel();
            throw new IllegalArgumentException("Unknown column index : " + columnIndex);
        }
    }
}
