package com.destroflyer.jme3.effekseer.virtual.renderer;

import com.jme3.scene.Geometry;
import lombok.*;

@Getter
@Setter
public class MergedParticleEmitterRendering extends ParticleEmitterRendering {
    private Geometry mergedGeometry;
}
