package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

@Builder
@Getter
@ToString
public class FCurveDynamicValues<RangeType extends Range> extends DynamicValues<RangeType> {
    private Object dummy;
}
