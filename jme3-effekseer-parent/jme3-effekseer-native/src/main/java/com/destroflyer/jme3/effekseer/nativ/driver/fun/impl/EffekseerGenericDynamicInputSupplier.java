package com.destroflyer.jme3.effekseer.nativ.driver.fun.impl;

import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerDynamicInputSetter;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerDynamicInputSupplier;

import java.util.ArrayList;

public class EffekseerGenericDynamicInputSupplier implements EffekseerDynamicInputSupplier{

    private ArrayList<Float> inputs = new ArrayList<>();

    public EffekseerGenericDynamicInputSupplier set(int index, Float value) {
        while (index >= inputs.size()) {
            inputs.add(Float.NaN);
        }
        if (value == null) {
            inputs.set(index, Float.NaN);
        } else {
            inputs.set(index, value);
        }
        while (Float.isNaN(inputs.get(inputs.size() - 1))) {
            inputs.remove(inputs.size() - 1);
        }
        return this;
    }

    public float get(int index) {
        return inputs.get(index);
    }

    @Override
    public void set(int handler, EffekseerDynamicInputSetter setter) {
        for (int i = 0; i < inputs.size(); i++) {
            setter.set(i, inputs.get(i));
        }
    }
}
