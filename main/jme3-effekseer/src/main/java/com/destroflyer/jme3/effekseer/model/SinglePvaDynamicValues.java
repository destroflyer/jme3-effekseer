package com.destroflyer.jme3.effekseer.model;

import lombok.*;

@Builder
@Getter
@ToString
public class SinglePvaDynamicValues<RangeType extends Range> extends DynamicValues<RangeType> {
    private Class<? extends RangeType> rangeClass;
    private Range1f base;
    private Range1f velocity;
    private Range1f acceleration;
}
