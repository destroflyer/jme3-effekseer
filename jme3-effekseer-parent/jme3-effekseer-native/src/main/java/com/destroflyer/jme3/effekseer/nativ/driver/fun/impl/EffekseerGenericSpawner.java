package com.destroflyer.jme3.effekseer.nativ.driver.fun.impl;

import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerEmissionCallback;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerEmissionCallback.CallbackType;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerSpawner;
import com.jme3.math.FastMath;

public class EffekseerGenericSpawner implements EffekseerSpawner {

    private int maxInstances = 1;
    private float initialDelay = 0;
    private float minDelay = 0;
    private float maxDelay = 0;
    private boolean loop = false;
    private boolean emittedFirst = false;
    private float delta = Float.NaN;
    private int instances = 0;
    private boolean spawnNow = false;

    @Override
    public EffekseerEmissionCallback spawn(float tpf) {
        if (instances > maxInstances) {
            return null;
        }

        if (!spawnNow) {
            if (emittedFirst && !loop) {
                return null;
            }

            if (Float.isNaN(delta)) {
                delta = initialDelay;
            }

            delta -= tpf;
            if (delta <= 0) {
                delta = FastMath.nextRandomFloat() * (maxDelay - minDelay) + minDelay;
            } else {
                return null;
            }
            emittedFirst = true;
        } else {
            spawnNow = false;
        }

        return (type, handle) -> {
            if (type == CallbackType.DESTROY_HANDLE) {
                instances--;
            } else if (type == CallbackType.SET_HANDLE) {
                instances++;
            }
        };
    }
}
