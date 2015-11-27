package common.domain;

import java.io.Serializable;

public class Client implements Serializable{
    private String name;
    private String phone;

    public Client(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
