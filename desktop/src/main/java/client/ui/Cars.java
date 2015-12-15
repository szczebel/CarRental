package client.ui;

import common.domain.Car;
import swingutils.EventListHolder;
import swingutils.components.table.TableFactory;
import swingutils.components.table.TablePanel;
import swingutils.components.table.descriptor.Columns;

class Cars extends EventListHolder<Car> {


    TablePanel<Car> createTable() {
        Columns<Car> columns = Columns.create(Car.class)
                .column("Registration", String.class, Car::getRegistration)
                .column("Model", String.class, Car::getModel)
                .column("Class", String.class, Car::getRentalClassName);
        return TableFactory.createTablePanel(getData(), columns);
    }
}
