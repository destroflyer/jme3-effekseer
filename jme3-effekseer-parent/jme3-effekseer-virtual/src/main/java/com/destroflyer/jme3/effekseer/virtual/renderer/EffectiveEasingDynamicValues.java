package com.destroflyer.jme3.effekseer.virtual.renderer;

import lombok.*;

@Builder
@Getter
@ToString
public class EffectiveEasingDynamicValues<VectorType> extends EffectiveDynamicValues<VectorType> {
    private VectorType start;
    private VectorType end;
}
