package com.destroflyer.jme3.effekseer.renderer;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import lombok.*;

@Getter
@Setter
@ToString
public class Particle {
    private ParticleEmitter emitter;
    private float startingLife;
    private float remainingLife;
    private Vector3f fixedParentLocation;
    private Quaternion fixedParentRotation;
    private Vector3f fixedParentScale;
    private EffectiveDynamicValues<Vector3f> effectiveLocationValues;
    private EffectiveDynamicValues<Vector3f> effectiveRotationValues;
    private EffectiveDynamicValues<Vector3f> effectiveScalingValues;
    private EffectiveDrawingValues effectiveDrawingValues;
    private EffectiveUvValues effectiveUvValues;
    private Transform transform;
    private Geometry geometry;
}
