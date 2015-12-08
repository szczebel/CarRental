package common.service;

import common.domain.RentalClass;

import java.util.List;

public interface RentalClassService {

    List<RentalClass> fetchAll();

    void create(RentalClass clazz);
}
