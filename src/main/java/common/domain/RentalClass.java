package common.domain;

public class RentalClass {
    private String name;
    private int hourlyRate;

    public RentalClass(String name, int hourlyRate) {
        this.name = name;
        this.hourlyRate = hourlyRate;
    }

    public String getName() {
        return name;
    }

    public int getHourlyRate() {
        return hourlyRate;
    }

    @Override
    public String toString() {
        return getName();
    }
}
