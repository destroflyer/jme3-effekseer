package com.destroflyer.jme3.effekseer.model;

import lombok.*;

@Builder
@Getter
@ToString
public class TrackDrawingValues extends DrawingValues {
    private float sizeFrontFixed;
    private float sizeMiddleFixed;
    private float sizeBackFixed;
    private ColorValues colorValuesLeft;
    private ColorValues colorValuesLeftCenter;
    private ColorValues colorValuesCenter;
    private ColorValues colorValuesCenterMiddle;
    private ColorValues colorValuesRight;
    private ColorValues colorValuesRightCenter;
}
