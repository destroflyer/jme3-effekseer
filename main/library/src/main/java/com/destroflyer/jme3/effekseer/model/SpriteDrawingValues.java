package com.destroflyer.jme3.effekseer.model;

import lombok.*;

@Builder
@Getter
@ToString
public class SpriteDrawingValues extends DrawingValues {
    private int billboard;
    private ColorValues colorValues;
    private SpriteVertexPositions vertexPositions;
}
