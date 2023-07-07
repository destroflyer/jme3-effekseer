package com.destroflyer.jme3.effekseer.nativ.driver;

import com.destroflyer.jme3.effekseer.nativ.driver.fun.*;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerEmissionCallback.CallbackType;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.impl.EffekseerGenericDynamicInputSupplier;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.impl.EffekseerGenericSpawner;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.impl.EffekseerPointFollowingSpatialShape;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Transform;
import com.jme3.scene.Spatial;
import com.jme3.util.clone.Cloner;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EffekseerEmissionDriverGeneric implements EffekseerEmissionDriver{

    protected transient Map<Integer,EffekseerEmissionCallback> instances = new HashMap<>();
    protected transient Set<Integer> instanceKeysRO = Collections.unmodifiableSet(instances.keySet());
    protected transient float tpf = 0;

    protected EffekseerEmitterShape shape = new EffekseerPointFollowingSpatialShape();
    protected EffekseerSpawner spawner = new EffekseerGenericSpawner();
    protected EffekseerDynamicInputSupplier dynamicInputSupplier = new EffekseerGenericDynamicInputSupplier();
    protected EffekseerEmissionUpdateListener updateListener = null;

    public EffekseerEmitterShape shape() {
        return shape;
    }

    public EffekseerEmissionDriverGeneric shape(EffekseerEmitterShape v) {
         shape = v;
         return this;
    }

    public void setUpdateListener(EffekseerEmissionUpdateListener up) {
        this.updateListener = up;
    }

    public EffekseerSpawner spawner() {
        return spawner;
    }

    public EffekseerEmissionDriverGeneric spawner(EffekseerSpawner v) {
        spawner = v;
        return this;
    }

    public EffekseerDynamicInputSupplier dynamicInputSupplier() {
        return dynamicInputSupplier;
    }

    public EffekseerEmissionDriverGeneric dynamicInputSupplier(EffekseerDynamicInputSupplier v) {
        dynamicInputSupplier = v;
        return this;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(shape,"shape",null);
        out.write(spawner,"emit",null);
        out.write(dynamicInputSupplier,"inputSetter",null);        
        out.write(updateListener,"updateListener",null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        shape = (EffekseerEmitterShape) in.readSavable("shape",null);
        spawner = (EffekseerSpawner) in.readSavable("emit",null);
        dynamicInputSupplier = (EffekseerDynamicInputSupplier) in.readSavable("inputSetter",null);
        updateListener = (EffekseerEmissionUpdateListener) in.readSavable("updateListener",null);
    }

    @Override
    public Object jmeClone() {
        EffekseerEmissionDriverGeneric clone;
        try {
            clone = (EffekseerEmissionDriverGeneric) super.clone();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return clone;
    }

    @Override
    public void cloneFields(Cloner cloner, Object original) {
        instances = new HashMap<>();
        shape = cloner.clone(shape);
        spawner = cloner.clone(spawner);
        dynamicInputSupplier = cloner.clone(dynamicInputSupplier);
    }

    @Override
    public void update(float tpf) {
        this.tpf = tpf;
        if (updateListener != null) {
            updateListener.onUpdate(tpf, instanceKeysRO);
        }
    }

    @Override
    public Integer tryEmit(EffekseerEmitFun emitInstanceAndGetHandle) {
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
    public Transform getInstanceTransform(int handle, Spatial spatial, float scale) {
        return shape.getTransform(handle, spatial,scale);
    }

    @Override
    public void setDynamicInputs(int handle, EffekseerDynamicInputSetterFun setter) {
        dynamicInputSupplier.set(handle, setter);
    }
}
