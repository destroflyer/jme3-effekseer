package com.destroflyer.jme3.effekseer.nativ.driver.fun;

public interface EffekseerEmissionCallback {

    enum CallbackType{
        SET_HANDLE,
        DESTROY_HANDLE
    }

    void call(CallbackType type, Integer handler);
}
