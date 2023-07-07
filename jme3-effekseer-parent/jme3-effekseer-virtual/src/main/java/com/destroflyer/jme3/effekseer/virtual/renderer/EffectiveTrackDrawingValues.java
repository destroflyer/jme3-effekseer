package com.destroflyer.jme3.effekseer.virtual.renderer;

import lombok.*;

@Builder
@Getter
@ToString
public class EffectiveTrackDrawingValues extends EffectiveDrawingValues {
    private EffectiveColorValues effectiveColorValuesLeftFixed;
    private EffectiveColorValues effectiveColorValuesLeftCenterFixed;
    private EffectiveColorValues effectiveColorValuesCenterFixed;
    private EffectiveColorValues effectiveColorValuesCenterMiddleFixed;
    private EffectiveColorValues effectiveColorValuesRightFixed;
    private EffectiveColorValues effectiveColorValuesRightCenterFixed;
}
