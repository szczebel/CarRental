package client.ui;

import common.domain.RentalClass;
import org.springframework.stereotype.Component;
import swingutils.EventListHolder;
import swingutils.components.table.TableFactory;
import swingutils.components.table.TablePanel;
import swingutils.components.table.descriptor.Columns;

import javax.swing.*;

@Component
public class RentalClasses extends EventListHolder<RentalClass> {

    TablePanel<RentalClass> createTable() {
        Columns<RentalClass> columns = Columns.create(RentalClass.class)
                .column("Name", String.class, RentalClass::getName)
                .column("Hourly rate", Integer.class, RentalClass::getHourlyRate)
                ;
        return TableFactory.createTablePanel(getData(), columns);
    }

    public ComboBoxModel<RentalClass> createComboBoxModel(boolean includeNull) {
        DefaultComboBoxModel<RentalClass> comboBoxModel = new DefaultComboBoxModel<>();
        if(includeNull) comboBoxModel.addElement(null);
        getData().forEach(comboBoxModel::addElement);
        return comboBoxModel;
    }
}
