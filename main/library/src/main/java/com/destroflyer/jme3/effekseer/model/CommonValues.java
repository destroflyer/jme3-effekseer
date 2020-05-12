package com.destroflyer.jme3.effekseer.model;

import lombok.*;

@Builder
@Getter
@ToString
public class CommonValues {
    private MaxGeneration maxGeneration;
    private Range1f life;
    private TransformInheritance locationInheritance;
    private TransformInheritance rotationInheritance;
    private TransformInheritance scalingInheritance;
    private boolean destroyAfterTime;
    private Range1f generationTimeOffset;
    private Range1f generationTime;
}
