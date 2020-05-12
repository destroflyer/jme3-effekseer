package com.destroflyer.jme3.effekseer.renderer;

import lombok.*;

@Getter
@Setter
public class EffectiveDynamicValues<VectorType> {
    private VectorType localBaseValue;
    private VectorType currentLocalRelativeValue;
}
