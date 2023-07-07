package com.destroflyer.jme3.effekseer.nativ.driver.fun;

import com.jme3.export.Savable;
import com.jme3.util.clone.JmeCloneable;

public interface EffekseerSpawner  extends Savable, JmeCloneable {

    EffekseerEmissionCallback spawn(float tpf);
}