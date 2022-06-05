package com.example.wpam_power_app;

import java.io.Serializable;

public class ProfileModel implements Serializable {
    String name;
    String surname;
    String weight;
    String height;
    String id;

    public ProfileModel(String name, String surname, String weight, String height, String id) {
        this.name=name;
        this.surname=surname;
        this.weight=weight;
        this.height=height;
        this.id = id;

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

    public String getId() {
        return id;
    }
}
