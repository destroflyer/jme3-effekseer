package com.destroflyer.jme3.effekseer.model;

import lombok.*;

@Builder
@Getter
@ToString
public class ScrollUvValues extends UvValues {
    private Range2f start;
    private Range2f size;
    private Range2f speed;
}
