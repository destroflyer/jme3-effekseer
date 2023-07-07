package com.destroflyer.jme3.effekseer.virtual.renderer;

import com.destroflyer.jme3.effekseer.virtual.model.ParticleNode;
import lombok.*;

@Getter
@Setter
@ToString
public class ParticleEmitter {
    private Particle parentParticle;
    private ParticleNode particleNode;
    private float remainingEmitDelay;
    private float spawnInterval;
    private float framesSinceLastEmit;
    private int generation;
}
