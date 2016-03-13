package mocks;

import common.domain.RentalClass;
import common.service.RentalClassService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component("rentalClassService")
public class MockRentalClassService implements RentalClassService {
    List<RentalClass> rentalClasses = new ArrayList<>();

    @Override
    public List<RentalClass> fetchAll() {
        return new ArrayList<>(rentalClasses);
    }

    @Override
    public void create(RentalClass rentalClass) {
        if (rentalClasses.stream().anyMatch(name(rentalClass)))
            throw new IllegalArgumentException("Class already exists: " + rentalClass.getName());
        rentalClasses.add(rentalClass);
    }

    private Predicate<RentalClass> name(RentalClass rentalClass) {
        return client -> client.getName().equals(rentalClass.getName());
    }
}
