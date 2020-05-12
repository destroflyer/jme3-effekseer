package com.destroflyer.jme3.effekseer.renderer;

import com.jme3.math.Vector2f;
import lombok.*;

@Builder
@Getter
@ToString
public class EffectiveScrollUvValues extends EffectiveUvValues {
    private Vector2f start;
    private Vector2f size;
    private Vector2f speed;
}
