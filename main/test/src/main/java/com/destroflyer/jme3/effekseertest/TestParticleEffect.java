package com.destroflyer.jme3.effekseertest;

import com.destroflyer.jme3.effekseer.model.ParticleEffect;
import lombok.*;

@Builder
@Getter
public class TestParticleEffect {
    private String directory;
    private String fileName;
    private ParticleEffect particleEffect;
    private TestParticleEffectInfo info;
}
