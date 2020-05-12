package com.destroflyer.jme3.effekseer.renderer;

import com.jme3.math.ColorRGBA;
import lombok.*;

@Builder
@Getter
@ToString
public class EffectiveFixedColorValues extends EffectiveColorValues {
    private ColorRGBA base;
}
