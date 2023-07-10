package com.destroflyer.jme3.effekseer.nativ.driver.fun;

import com.jme3.math.Transform;
import com.jme3.scene.Spatial;

public interface EffekseerEmitterShape {

    Transform getTransform(int handler, Spatial spatial);
}
