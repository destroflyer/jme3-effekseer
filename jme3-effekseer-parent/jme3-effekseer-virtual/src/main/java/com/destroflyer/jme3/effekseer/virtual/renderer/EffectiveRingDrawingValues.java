package com.destroflyer.jme3.effekseer.virtual.renderer;

import com.jme3.math.Vector2f;
import lombok.*;

@Builder
@Getter
@ToString
public class EffectiveRingDrawingValues extends EffectiveDrawingValues {
    private EffectiveDynamicValues<Float> effectiveViewingAngleValues;
    private EffectiveDynamicValues<Vector2f> effectiveInnerPositionValues;
    private EffectiveDynamicValues<Vector2f> effectiveOuterPositionValues;
    private EffectiveDynamicValues<Float> effectiveCenterRatioValues;
    private EffectiveColorValues effectiveColorValuesInner;
    private EffectiveColorValues effectiveColorValuesCenter;
    private EffectiveColorValues effectiveColorValuesOuter;
}
