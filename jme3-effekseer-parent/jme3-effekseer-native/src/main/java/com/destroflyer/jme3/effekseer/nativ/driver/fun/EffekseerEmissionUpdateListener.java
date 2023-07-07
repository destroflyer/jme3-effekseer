package com.destroflyer.jme3.effekseer.nativ.driver.fun;

import com.jme3.export.Savable;
import com.jme3.util.clone.JmeCloneable;

import java.util.Set;

public interface EffekseerEmissionUpdateListener extends Savable, JmeCloneable {

    void onUpdate(float tpf, Set<Integer> instanceKeysRO);
}