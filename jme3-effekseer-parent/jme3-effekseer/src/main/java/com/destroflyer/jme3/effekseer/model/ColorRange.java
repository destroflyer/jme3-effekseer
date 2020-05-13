package com.destroflyer.jme3.effekseer.model;

import lombok.*;

@Builder
@Getter
@ToString
public class ColorRange {
    private ColorSpace colorSpace;
    private Range1f red;
    private Range1f green;
    private Range1f blue;
    private Range1f alpha;
}
