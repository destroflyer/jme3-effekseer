package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

@Builder
@Getter
@ToString
public class Range3f extends Range {
    private Range1f x;
    private Range1f y;
    private Range1f z;
}
