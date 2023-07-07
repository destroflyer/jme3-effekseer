package com.destroflyer.jme3.effekseer.nativ.driver.fun.impl;

import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerEmissionCallback;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerEmissionCallback.CallbackType;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerSpawner;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.util.clone.Cloner;

import java.io.IOException;

public class EffekseerGenericSpawner implements EffekseerSpawner {

    private int maxInstances = 1;
    private float initialDelay = 0;
    private float minDelay = 0;
    private float maxDelay = 0;
    private boolean loop = false;

    private transient boolean emittedFirst = false;
    private transient float delta = Float.NaN;
    private transient int instances = 0;

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule c = ex.getCapsule(this);
        c.write(maxInstances,"maxInstances",1);
        c.write(initialDelay,"initialDelay",0);
        c.write(maxDelay,"maxDelay",0);
        c.write(minDelay,"minDelay",0);
        c.write(loop,"loop",false);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule c = im.getCapsule(this);
        maxInstances = c.readInt("maxInstances",1);
        initialDelay = c.readFloat("initialDelay",0);
        maxDelay = c.readFloat("maxDelay",0);
        minDelay = c.readFloat("minDelay",0);
        loop = c.readBoolean("loop",false);
    }

    @Override
    public Object jmeClone() {
        EffekseerGenericSpawner clone = new EffekseerGenericSpawner();
        clone.maxInstances = maxInstances;
        clone.initialDelay = initialDelay;
        clone.maxDelay = maxDelay;
        clone.minDelay = minDelay;
        clone.loop = loop;
        return clone;
    }

    @Override
    public void cloneFields(Cloner cloner, Object original) {

    }

    private boolean spawnNow = false;
    public EffekseerGenericSpawner spawnNow() {
        spawnNow = true;
        return this;
    }

    private boolean autoSpawner = true;
    public EffekseerGenericSpawner autoSpawner(boolean v) {
        autoSpawner = v;
        return this;
    }

    public boolean autoSpawner() {
        return autoSpawner;
    }

    @Override
    public EffekseerEmissionCallback spawn(float tpf) {
        if (instances > maxInstances) {
            return null;
        }

        if (!spawnNow) {
            if (!autoSpawner) {
                return null;
            }
            if (emittedFirst && !loop) {
                return null;
            }

            if(Float.isNaN(delta)) {
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

    public EffekseerGenericSpawner loop(boolean v) {
        loop = v;
        return this;
    }

    public EffekseerGenericSpawner delay(float min, float max, float initial) {
        minDelay = min;
        initialDelay = initial;
        maxDelay = max;
        return this;
    }

    public EffekseerGenericSpawner maxInstances(int v) {
        maxInstances = v;
        return this;
    }
}
