package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

@Builder
@Getter
@ToString
public class Range1f extends Range {
    private float min;
    private float max;
    private float center;
}
