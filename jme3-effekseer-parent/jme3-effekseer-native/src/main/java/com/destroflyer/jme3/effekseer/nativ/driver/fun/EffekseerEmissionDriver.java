package com.destroflyer.jme3.effekseer.nativ.driver.fun;

import com.jme3.math.Transform;
import com.jme3.scene.Spatial;

public interface EffekseerEmissionDriver {

    void update(float tpf);

    Integer tryEmit(EffekseerEmitter emitInstanceAndGetHandle);

    void setDynamicInputs(int handle, EffekseerDynamicInputSetter set);

    void destroy(int handle);

    Transform getInstanceTransform(int handle, Spatial spatial);
}
