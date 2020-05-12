package com.destroflyer.jme3.effekseer.model;

import com.jme3.math.Vector2f;
import lombok.*;

@Builder
@Getter
@ToString
public class SpriteVertexPositions {
    private Vector2f topLeft;
    private Vector2f topRight;
    private Vector2f bottomLeft;
    private Vector2f bottomRight;
}
