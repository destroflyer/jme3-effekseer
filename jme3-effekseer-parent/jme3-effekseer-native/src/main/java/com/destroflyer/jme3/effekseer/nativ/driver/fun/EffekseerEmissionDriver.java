package com.destroflyer.jme3.effekseer.nativ.driver.fun;

import com.jme3.export.Savable;
import com.jme3.math.Transform;
import com.jme3.scene.Spatial;
import com.jme3.util.clone.JmeCloneable;

public interface EffekseerEmissionDriver extends Savable, JmeCloneable {

    void update(float tpf);

    Integer tryEmit(EffekseerEmitFun emitInstanceAndGetHandle);

    void setDynamicInputs( int handle, EffekseerDynamicInputSetterFun set);

    void destroy(int handle);

    Transform getInstanceTransform(int handle, Spatial spatial, float scale);
}