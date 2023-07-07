package com.destroflyer.jme3.effekseer.virtual.model;

import com.jme3.math.Vector2f;
import lombok.*;

@Builder
@Getter
@ToString
public class FixedUvValues extends UvValues {
    private Vector2f start;
    private Vector2f size;
}
