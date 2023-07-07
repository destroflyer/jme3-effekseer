package com.destroflyer.jme3.effekseer.virtual.renderer;

import lombok.*;

@Getter
@Setter
public class EffectiveDynamicValues<VectorType> {
    private VectorType localBaseValue;
    private VectorType currentLocalRelativeValue;
}
