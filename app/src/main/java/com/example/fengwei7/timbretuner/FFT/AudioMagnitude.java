package com.example.fengwei7.timbretuner.FFT;


import io.realm.RealmObject;

public class AudioMagnitude extends RealmObject {
    double mMagnitude;

    public double getMagnitude() {
        return mMagnitude;
    }

    public void setMagnitude(double magnitude) {
        mMagnitude = magnitude;
    }
}
