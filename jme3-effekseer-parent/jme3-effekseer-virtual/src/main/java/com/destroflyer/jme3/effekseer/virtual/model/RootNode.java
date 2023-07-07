package com.destroflyer.jme3.effekseer.virtual.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@ToString
public class RootNode {
    private String name;
    private List<ParticleNode> children;
    private boolean rendered;
}
