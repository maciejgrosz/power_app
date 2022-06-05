package com.example.wpam_power_app;

import java.io.Serializable;

public class ResultModel {
    String powerVal;
    String accelVal;
    String veloVal;


    public ResultModel(String powerVal, String accelVal, String veloVal) {
        this.powerVal = powerVal;
        this.accelVal = accelVal;
        this.veloVal = veloVal;

    }

    public String getPowerVal() {
        return powerVal;
    }

    public String getAccelVal() {
        return accelVal;
    }

    public String getVeloVal() {
        return veloVal;
    }
}
