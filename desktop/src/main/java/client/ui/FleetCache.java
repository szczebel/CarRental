package client.ui;

import client.PubSub;
import common.domain.Car;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class FleetCache extends Cars {

    @JmsListener(destination = PubSub.NEW_CAR_TOPIC)
    void onNewCar(Car car) {
        SwingUtilities.invokeLater(() -> addCar(car));
    }

    void addCar(Car car) {
        getData().add(car);
    }
}
