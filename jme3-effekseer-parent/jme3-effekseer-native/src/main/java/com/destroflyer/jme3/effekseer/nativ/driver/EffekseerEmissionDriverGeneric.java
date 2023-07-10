package com.destroflyer.jme3.effekseer.nativ.driver;

import com.destroflyer.jme3.effekseer.nativ.driver.fun.*;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerEmissionCallback.CallbackType;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.impl.EffekseerGenericDynamicInputSupplier;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.impl.EffekseerGenericSpawner;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.impl.EffekseerPointFollowingSpatialShape;
import com.jme3.math.Transform;
import com.jme3.scene.Spatial;

import java.util.HashMap;
import java.util.Map;

public class EffekseerEmissionDriverGeneric implements EffekseerEmissionDriver{

    private Map<Integer, EffekseerEmissionCallback> instances = new HashMap<>();
    private float tpf = 0;
    private EffekseerEmissionUpdateListener updateListener = null;
    private EffekseerSpawner spawner = new EffekseerGenericSpawner();
    private EffekseerEmitterShape shape = new EffekseerPointFollowingSpatialShape();
    private EffekseerDynamicInputSupplier dynamicInputSupplier = new EffekseerGenericDynamicInputSupplier();

    @Override
    public void update(float tpf) {
        this.tpf = tpf;
        if (updateListener != null) {
            updateListener.onUpdate(tpf, instances.keySet());
        }
    }

    @Override
    public Integer tryEmit(EffekseerEmitter emitInstanceAndGetHandle) {
        EffekseerEmissionCallback callback = spawner.spawn(tpf);
        if (callback != null) {
            Integer handle = emitInstanceAndGetHandle.emit();
            callback.call(CallbackType.SET_HANDLE, handle);
            instances.put(handle,callback);
            return handle;
        }
        return null;
    }

    @Override
    public void destroy(int handle) {
        EffekseerEmissionCallback callback = instances.remove(handle);
        if (callback != null) {
            callback.call(CallbackType.DESTROY_HANDLE,handle);
        }
    }

    @Override
    public Transform getInstanceTransform(int handle, Spatial spatial) {
        return shape.getTransform(handle, spatial);
    }

    @Override
    public void setDynamicInputs(int handle, EffekseerDynamicInputSetter setter) {
        dynamicInputSupplier.set(handle, setter);
    }
}
