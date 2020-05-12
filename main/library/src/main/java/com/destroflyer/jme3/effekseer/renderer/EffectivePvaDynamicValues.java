package com.destroflyer.jme3.effekseer.renderer;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
public class EffectivePvaDynamicValues<VectorType> extends EffectiveDynamicValues<VectorType> {
    private VectorType velocity;
    private VectorType acceleration;
}
