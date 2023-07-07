package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

@Builder
@Getter
@ToString
public class RingDrawingValues extends DrawingValues {
    private int billboard;
    private int vertexCount;
    private DynamicValues<Range1f> viewingAngleValues;
    private DynamicValues<Range2f> innerPositionValues;
    private DynamicValues<Range2f> outerPositionValues;
    private DynamicValues<Range1f> centerRatioValues;
    private ColorValues colorValuesInner;
    private ColorValues colorValuesCenter;
    private ColorValues colorValuesOuter;
}
