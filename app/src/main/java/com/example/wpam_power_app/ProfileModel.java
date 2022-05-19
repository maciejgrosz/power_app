package com.example.wpam_power_app;

import java.io.Serializable;

public class ProfileModel implements Serializable {
    String name;
    String surname;
    String weight;
    String height;

    public ProfileModel(String name, String surname, String weight, String height) {
        this.name=name;
        this.surname=surname;
        this.weight=weight;
        this.height=height;

    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }
}
