package common.domain;

import java.io.Serializable;

public class RentalClass implements Serializable {
    private String name;
    private int hourlyRate;

    public RentalClass() {}

    public RentalClass(String name, int hourlyRate) {
        this.name = name;
        this.hourlyRate = hourlyRate;
    }

    public String getName() {
        return name;
    }

    public Integer getHourlyRate() {
        return hourlyRate;
    }
}
