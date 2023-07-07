package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

@Builder
@Getter
@ToString
public class ParticleEffect {
    private String directory;
    private RootNode root;
    private int startFrame;
    private int endFrame;
    private boolean isLoop;
}
