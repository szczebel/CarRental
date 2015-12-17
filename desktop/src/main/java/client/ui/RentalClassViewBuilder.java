package client.ui;

import common.domain.RentalClass;
import common.service.RentalClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.background.BackgroundOperation;
import swingutils.components.table.TablePanel;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

import static swingutils.components.ComponentFactory.button;
import static swingutils.layout.LayoutBuilders.borderLayout;
import static swingutils.layout.LayoutBuilders.flowLayout;

@Component
public class RentalClassViewBuilder {

    @Autowired RentalClassService rentalClassService;
    @Autowired RentalClasses rentalClasses;

    public JComponent build() {

        TablePanel<RentalClass> table = rentalClasses.createTable();

        refresh(rentalClasses::setData);
        return borderLayout()
                .north(
                        flowLayout(
                                button("Refresh", () -> refresh(rentalClasses::setData)),
                                button("Add...", () -> addClicked(table.getScrollPane(), rentalClasses)),
                                table.getToolbar()
                        ))
                .center(table.getScrollPane())
                .build();
    }

    private void addClicked(JComponent panel, RentalClasses tableModel) {
        String name = JOptionPane.showInputDialog(panel, "Name", "Add rental class", JOptionPane.QUESTION_MESSAGE);
        String rate = JOptionPane.showInputDialog(panel, "Hourly rate", "Add rental class", JOptionPane.QUESTION_MESSAGE);
        BackgroundOperation.execute(
                () -> rentalClassService.create(new RentalClass(name, Integer.parseInt(rate))),
                () -> refresh(tableModel::setData)
        );
    }

    private void refresh(Consumer<List<RentalClass>> consumer) {
        BackgroundOperation.execute(
                rentalClassService::fetchAll,
                consumer
        );
    }

}
