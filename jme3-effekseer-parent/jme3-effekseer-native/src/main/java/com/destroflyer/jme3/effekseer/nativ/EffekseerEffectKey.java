package com.destroflyer.jme3.effekseer.nativ;

import Effekseer.swig.EffekseerEffectCore;
import com.jme3.asset.AssetKey;

public class EffekseerEffectKey extends AssetKey<EffekseerEffectCore> {

    public EffekseerEffectKey(String path) {
        super(path);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof EffekseerEffectKey && super.equals(other);
    }
}
