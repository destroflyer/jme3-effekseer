package com.destroflyer.jme3.effekseer.virtual.model;

import com.jme3.math.ColorRGBA;
import lombok.*;

@Builder
@Getter
@ToString
public class FixedColorValues extends ColorValues {
    private ColorRGBA base;
}
