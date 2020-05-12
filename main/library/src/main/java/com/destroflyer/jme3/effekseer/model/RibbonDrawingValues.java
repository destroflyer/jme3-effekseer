package com.destroflyer.jme3.effekseer.model;

import com.jme3.math.Vector2f;
import lombok.*;

@Builder
@Getter
@ToString
public class RibbonDrawingValues extends DrawingValues {
    private Vector2f positionLeftRight;
    private RibbonParticleDrawingValues particleDrawingValues;
}
