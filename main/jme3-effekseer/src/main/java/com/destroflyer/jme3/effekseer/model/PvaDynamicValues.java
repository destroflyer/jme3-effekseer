package com.destroflyer.jme3.effekseer.model;

import lombok.*;

@Builder
@Getter
@ToString
public class PvaDynamicValues<RangeType extends Range> extends DynamicValues<RangeType> {
    private RangeType base;
    private RangeType velocity;
    private RangeType acceleration;
}
