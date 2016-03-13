package client.ui;

import common.domain.Car;
import common.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import swingutils.background.BackgroundOperation;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Component
public class FleetCache {

    @Autowired
    FleetService fleetService;

    List<Car> fleet;

    void setData(List<Car> fleet) {
        this.fleet = fleet;
    }

    public void reload(Consumer<List<Car>> callWhenDone) {
        BackgroundOperation.execute(fleetService::fetchAll,
                result -> {
                    setData(result);
                    callWhenDone.accept(getFleet());
                }
        );
    }

    public List<Car> getFleet() {
        if (fleet != null) return Collections.unmodifiableList(fleet);
        else {
            System.err.println("Cache not initialized, reloading");
            reload(f->{});
            return Collections.emptyList();
        }
    }
}
