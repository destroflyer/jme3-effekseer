package com.destroflyer.jme3.effekseer.test;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TestParticleEffectInfo {
    @Builder.Default
    private int quality = 0;
    @Builder.Default
    private float frameLength = (1f / 48);
    @Builder.Default
    private float cameraAngle = 0;
    @Builder.Default
    private float cameraDistance = 20;
    @Builder.Default
    private float cameraHeight = 10;
    @Builder.Default
    private float cameraSpeed = 0;
}
