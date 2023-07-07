package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

@Builder
@Getter
@ToString
public class RandomDynamicValues<RangeType extends Range> extends DynamicValues<RangeType> {
    private RangeType base;
}
