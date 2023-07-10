package com.destroflyer.jme3.effekseer.nativ.driver.fun.impl;

import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerEmitterShape;
import com.jme3.math.Transform;
import com.jme3.scene.Spatial;

public class EffekseerPointFollowingSpatialShape implements EffekseerEmitterShape {

    @Override
    public Transform getTransform(int handler, Spatial spatial) {
        return spatial.getWorldTransform();
    }
}
