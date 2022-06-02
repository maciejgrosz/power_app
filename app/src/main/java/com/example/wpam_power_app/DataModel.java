package com.example.wpam_power_app;

public class DataModel {
    double v;
    double a;
    double pLift;
    double tc;
    double shift;


    public DataModel(double v, double a, double pLift, double tc, double shift) {
        this.v = v;
        this.a = a;
        this.pLift = pLift;
        this.tc = tc;

    }


    public double getV() {
        return v;
    }

    public double getA() {
        return a;
    }

    public double getShift() {
        return shift;
    }

    public double getpLift() {
        return pLift;
    }

    public double getTc() {
        return tc;
    }
}
