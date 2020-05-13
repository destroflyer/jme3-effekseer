package com.destroflyer.jme3.effekseer.renderer;

import lombok.*;

@Builder
@Getter
@ToString
public class EffectiveFixedDynamicValues<VectorType> extends EffectiveDynamicValues<VectorType> {
    private VectorType base;
}
