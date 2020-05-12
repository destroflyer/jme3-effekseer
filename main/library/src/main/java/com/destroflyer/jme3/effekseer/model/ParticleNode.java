package com.destroflyer.jme3.effekseer.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@ToString
public class ParticleNode {
    private String name;
    private List<ParticleNode> children;
    private boolean rendered;
    private CommonValues commonValues;
    private DynamicValues<Range3f> locationValues;
    private DynamicValues<Range3f> rotationValues;
    private DynamicValues<Range3f> scalingValues;
    private RendererCommonValues rendererCommonValues;
    private DrawingValues drawingValues;
}
