package com.destroflyer.jme3.effekseer.model;

import lombok.*;

@Builder
@Getter
@ToString
public class ParticleEffect {
    private RootNode root;
    private int startFrame;
    private int endFrame;
    private boolean isLoop;
}
