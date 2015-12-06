package client.ui;

import client.ui.util.BackgroundOperation;
import common.domain.RentalClass;
import common.service.RentalClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

import static client.ui.util.GuiHelper.*;

@Component
public class RentalClassViewBuilder {

    @Autowired RentalClassService rentalClassService;
    @Autowired RentalClasses tableModel;

    public JComponent build() {

        refresh(tableModel);
        JTable table = new JTable(tableModel);

        return borderLayout()
                .north(
                        toolbar(
                                button("Refresh", () -> refresh(tableModel)),
                                button("Add...", () -> addCLicked(table, tableModel))
                        ))
                .center(inScrollPane(table))
                .build();
    }

    private void addCLicked(JComponent panel, RentalClasses tableModel) {
        String name = JOptionPane.showInputDialog(panel, "Name", "Add rental class", JOptionPane.QUESTION_MESSAGE);
        String rate = JOptionPane.showInputDialog(panel, "Hourly rate", "Add rental class", JOptionPane.QUESTION_MESSAGE);
        BackgroundOperation.execute(
                () -> rentalClassService.create(new RentalClass(name, Integer.parseInt(rate))),
                () -> refresh(tableModel)
        );
    }

    private void refresh(RentalClasses tableModel) {
        BackgroundOperation.execute(
                rentalClassService::fetchAll,
                tableModel::setData
        );
    }

}
