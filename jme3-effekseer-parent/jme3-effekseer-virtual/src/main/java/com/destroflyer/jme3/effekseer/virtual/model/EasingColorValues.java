package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

@Builder
@Getter
@ToString
public class EasingColorValues extends ColorValues {
    private ColorRange start;
    private ColorRange end;
}
