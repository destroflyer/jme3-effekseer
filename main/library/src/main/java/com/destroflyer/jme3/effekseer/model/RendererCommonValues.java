package com.destroflyer.jme3.effekseer.model;

import lombok.*;

@Builder
@Getter
@ToString
public class RendererCommonValues {
    private String colorTexture;
    private int alphaBlend;
    private boolean zWrite;
    private boolean zTest;
    private FadeValues fadeIn;
    private FadeValues fadeOut;
    private UvValues uvValues;
    private float distortionIntensity;
}
