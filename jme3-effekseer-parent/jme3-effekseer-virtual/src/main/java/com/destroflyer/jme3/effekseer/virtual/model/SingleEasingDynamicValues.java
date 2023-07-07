package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

@Builder
@Getter
@ToString
public class SingleEasingDynamicValues<RangeType extends Range> extends DynamicValues<RangeType> {
    private Class<? extends RangeType> rangeClass;
    private Range1f start;
    private Range1f end;
}
