package com.ourdreamit.blooddonationproject.utils;

import android.view.animation.Interpolator;

/**
 * Created by anismizi on 4/13/17.
 */

public class BounceInterpolator implements Interpolator {
    double mAmplitude = 1;
    double mFrequency = 10;

    public BounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }
    @Override
    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}
