package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

@Builder
@Getter
@ToString
public class SpriteDrawingValues extends DrawingValues {
    private int billboard;
    private ColorValues colorValues;
    private SpriteVertexPositions vertexPositions;
}
