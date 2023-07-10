package com.destroflyer.jme3.effekseer.nativ.driver.fun;

import java.util.Set;

public interface EffekseerEmissionUpdateListener {

    void onUpdate(float tpf, Set<Integer> instanceKeysRO);
}
