package com.destroflyer.jme3.effekseer.renderer;

import com.jme3.math.ColorRGBA;
import lombok.*;

@Builder
@Getter
@ToString
public class EffectiveEasingColorValues extends EffectiveColorValues {
    private ColorRGBA start;
    private ColorRGBA end;
}
