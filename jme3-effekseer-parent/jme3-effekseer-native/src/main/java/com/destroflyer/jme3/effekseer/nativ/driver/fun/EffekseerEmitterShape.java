package com.destroflyer.jme3.effekseer.nativ.driver.fun;

import com.jme3.export.Savable;
import com.jme3.math.Transform;
import com.jme3.scene.Spatial;
import com.jme3.util.clone.JmeCloneable;

public interface EffekseerEmitterShape extends Savable, JmeCloneable {

    Transform getTransform(int handler, Spatial spatial, float scale);
}
