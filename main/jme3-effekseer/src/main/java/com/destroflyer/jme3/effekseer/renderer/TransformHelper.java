package com.destroflyer.jme3.effekseer.renderer;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

import static com.destroflyer.jme3.effekseer.renderer.DynamicValuesHelper.getCurrentLocalValue3f;

public class TransformHelper {

    static Vector3f getWorldLocation(Particle particle) {
        Vector3f parentLocation = getEffectiveParentLocation(particle);
        Quaternion parentRotation = getEffectiveParentRotation(particle);
        Vector3f localLocation = getCurrentLocalValue3f(particle.getEffectiveLocationValues());
        return getWorldLocation(parentLocation, parentRotation, localLocation);
    }

    static Vector3f getWorldLocation(Particle particle, Vector3f localLocation) {
        return getWorldLocation(particle.getTransform().getTranslation(), particle.getTransform().getRotation(), localLocation);
    }

    private static Vector3f getWorldLocation(Vector3f parentLocation, Quaternion parentRotation, Vector3f localLocation) {
        return parentLocation.add(parentRotation.mult(localLocation));
    }

    static Quaternion getWorldRotation(Particle particle) {
        Quaternion parentRotation = getEffectiveParentRotation(particle);

        Vector3f localRotation = getCurrentLocalValue3f(particle.getEffectiveRotationValues()).mult(FastMath.DEG_TO_RAD);
        Quaternion newRotation = parentRotation.clone();
        newRotation.multLocal(new Quaternion().fromAngles(localRotation.getX(), localRotation.getY(), 0));
        newRotation.multLocal(new Quaternion().fromAngles(0, 0, localRotation.getZ()));
        return newRotation;
    }

    static Vector3f getWorldScale(Particle particle) {
        Vector3f parentScale = getEffectiveParentScale(particle);

        Vector3f localScale = getCurrentLocalValue3f(particle.getEffectiveScalingValues());
        return parentScale.mult(localScale);
    }

    private static Vector3f getEffectiveParentLocation(Particle particle) {
        return ((particle.getFixedParentLocation() != null) ? particle.getFixedParentLocation() : getParentTransform(particle).getTranslation());
    }

    private static Quaternion getEffectiveParentRotation(Particle particle) {
        return ((particle.getFixedParentRotation() != null) ? particle.getFixedParentRotation() : getParentTransform(particle).getRotation());
    }

    private static Vector3f getEffectiveParentScale(Particle particle) {
        return ((particle.getFixedParentScale() != null) ? particle.getFixedParentScale() : getParentTransform(particle).getScale());
    }

    static Transform getParentTransform(Particle particle) {
        Particle parentParticle = particle.getEmitter().getParentParticle();
        return ((parentParticle != null) ? parentParticle.getTransform() : Transform.IDENTITY);
    }
}
