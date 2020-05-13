package com.destroflyer.jme3.effekseer.model;

import lombok.*;

@Builder
@Getter
@ToString
public class FixedDynamicValues<RangeType extends Range> extends DynamicValues<RangeType> {
    private RangeType base;
}
