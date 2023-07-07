package com.destroflyer.jme3.effekseer.virtual.renderer;

import com.destroflyer.jme3.effekseer.virtual.model.*;
import com.destroflyer.jme3.effekseer.virtual.reader.ZeroValues;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class DynamicValuesHelper {

    static EffectiveDynamicValues generateEffectiveDynamicValues(DynamicValues dynamicValues) {
        EffectiveDynamicValues effectiveDynamicValues = null;
        Object localBaseValue = null;
        if (dynamicValues instanceof FixedDynamicValues) {
            FixedDynamicValues fixedDynamicValues = (FixedDynamicValues) dynamicValues;
            Object base = RangeValues.generateRangeValue(fixedDynamicValues.getBase());
            effectiveDynamicValues = EffectiveFixedDynamicValues.builder()
                    .base(base)
                    .build();
            localBaseValue = base;
        } else if (dynamicValues instanceof PvaDynamicValues) {
            PvaDynamicValues pvaDynamicValues = (PvaDynamicValues) dynamicValues;
            Object base = RangeValues.generateRangeValue(pvaDynamicValues.getBase());
            Object velocity = RangeValues.generateRangeValue(pvaDynamicValues.getVelocity());
            Object acceleration = RangeValues.generateRangeValue(pvaDynamicValues.getAcceleration());
            effectiveDynamicValues = EffectivePvaDynamicValues.builder()
                    .velocity(velocity)
                    .acceleration(acceleration)
                    .build();
            localBaseValue = base;
        } else if (dynamicValues instanceof SinglePvaDynamicValues) {
            SinglePvaDynamicValues singlePvaDynamicValues = (SinglePvaDynamicValues) dynamicValues;
            Object base = RangeValues.generateSingleRangeValue(singlePvaDynamicValues.getRangeClass(), singlePvaDynamicValues.getBase());
            Object velocity = RangeValues.generateSingleRangeValue(singlePvaDynamicValues.getRangeClass(), singlePvaDynamicValues.getVelocity());
            Object acceleration = RangeValues.generateSingleRangeValue(singlePvaDynamicValues.getRangeClass(), singlePvaDynamicValues.getAcceleration());
            effectiveDynamicValues = EffectivePvaDynamicValues.builder()
                    .velocity(velocity)
                    .acceleration(acceleration)
                    .build();
            localBaseValue = base;
        } else if (dynamicValues instanceof EasingDynamicValues) {
            EasingDynamicValues easingDynamicValues = (EasingDynamicValues) dynamicValues;
            Object start = RangeValues.generateRangeValue(easingDynamicValues.getStart());
            Object end = RangeValues.generateRangeValue(easingDynamicValues.getEnd());
            effectiveDynamicValues = EffectiveEasingDynamicValues.builder()
                    .start(start)
                    .end(end)
                    .build();
            localBaseValue = ZeroValues.getZeroValueByValueClass(start.getClass());
        } else if (dynamicValues instanceof SingleEasingDynamicValues) {
            SingleEasingDynamicValues singleEasingDynamicValues = (SingleEasingDynamicValues) dynamicValues;
            Object start = RangeValues.generateSingleRangeValue(singleEasingDynamicValues.getRangeClass(), singleEasingDynamicValues.getStart());
            Object end = RangeValues.generateSingleRangeValue(singleEasingDynamicValues.getRangeClass(), singleEasingDynamicValues.getEnd());
            effectiveDynamicValues = EffectiveEasingDynamicValues.builder()
                    .start(start)
                    .end(end)
                    .build();
            localBaseValue = ZeroValues.getZeroValueByValueClass(start.getClass());
        } else if (dynamicValues instanceof RandomDynamicValues) {
            RandomDynamicValues randomDynamicValues = (RandomDynamicValues) dynamicValues;
            Object base = RangeValues.generateRangeValue(randomDynamicValues.getBase());
            effectiveDynamicValues = EffectiveFixedDynamicValues.builder()
                    .base(base)
                    .build();
            localBaseValue = base;
        } else if (dynamicValues instanceof FCurveDynamicValues) {
            FCurveDynamicValues fCurveDynamicValues = (FCurveDynamicValues) dynamicValues;
            effectiveDynamicValues = EffectiveFCurveDynamicValues.builder()
                    .build();
            localBaseValue = fCurveDynamicValues.getDummy();
        }
        effectiveDynamicValues.setLocalBaseValue(localBaseValue);
        effectiveDynamicValues.setCurrentLocalRelativeValue(ZeroValues.getZeroValueByValueClass(localBaseValue.getClass()));
        return effectiveDynamicValues;
    }

    static void updateCurrentLocalRelativeDynamicValue(Particle particle, EffectiveDynamicValues effectiveDynamicValues, float frameProgress) {
        Object newRelativeValue = null;
        Object currentLocalRelativeValue = effectiveDynamicValues.getCurrentLocalRelativeValue();
        if (effectiveDynamicValues instanceof EffectivePvaDynamicValues) {
            EffectivePvaDynamicValues effectivePvaDynamicValues = (EffectivePvaDynamicValues) effectiveDynamicValues;
            Object acceleration = effectivePvaDynamicValues.getAcceleration();
            Object velocity = effectivePvaDynamicValues.getVelocity();
            if (acceleration instanceof Vector3f) {
                Vector3f acceleration3f = (Vector3f) acceleration;
                Vector3f velocity3f = (Vector3f) velocity;
                Vector3f currentLocalRelativeValue3f = (Vector3f) currentLocalRelativeValue;

                velocity3f.addLocal(acceleration3f.mult(frameProgress));
                newRelativeValue = currentLocalRelativeValue3f.add(velocity3f.mult(frameProgress));
            } else if (acceleration instanceof Vector2f) {
                Vector2f acceleration2f = (Vector2f) acceleration;
                Vector2f velocity2f = (Vector2f) velocity;
                Vector2f currentLocalRelativeValue2f = (Vector2f) currentLocalRelativeValue;

                velocity2f.addLocal(acceleration2f.mult(frameProgress));
                newRelativeValue = currentLocalRelativeValue2f.add(velocity2f.mult(frameProgress));
            } else if (acceleration instanceof Float) {
                Float acceleration1f = (Float) acceleration;
                Float velocity1f = (Float) velocity;
                Float currentLocalRelativeValue1f = (Float) currentLocalRelativeValue;

                velocity1f += (acceleration1f * frameProgress);
                effectivePvaDynamicValues.setVelocity(velocity1f);
                newRelativeValue = (currentLocalRelativeValue1f + (velocity1f * frameProgress));
            }
        } else if (effectiveDynamicValues instanceof EffectiveEasingDynamicValues) {
            EffectiveEasingDynamicValues effectiveEasingDynamicValues = (EffectiveEasingDynamicValues) effectiveDynamicValues;
            Object start = effectiveEasingDynamicValues.getStart();
            Object end = effectiveEasingDynamicValues.getEnd();
            float progress = (1 - (particle.getRemainingLife() / particle.getStartingLife()));
            if (start instanceof Vector3f) {
                Vector3f start3f = (Vector3f) start;
                Vector3f end3f = (Vector3f) end;

                newRelativeValue = new Vector3f(start3f).interpolateLocal(end3f, progress);
            } else if (start instanceof Vector2f) {
                Vector2f start2f = (Vector2f) start;
                Vector2f end2f = (Vector2f) end;

                newRelativeValue = new Vector2f(start2f).interpolateLocal(end2f, progress);
            } else if (start instanceof Float) {
                Float start1f = (Float) start;
                Float end1f = (Float) end;

                newRelativeValue = ((1 - progress) * start1f) + (progress * end1f);
            }
        } else {
            newRelativeValue = currentLocalRelativeValue;
        }
        effectiveDynamicValues.setCurrentLocalRelativeValue(newRelativeValue);
    }

    static Vector3f getCurrentLocalValue3f(EffectiveDynamicValues<Vector3f> effectiveDynamicValues) {
        Vector3f baseValue = effectiveDynamicValues.getLocalBaseValue();
        Vector3f relativeValue = effectiveDynamicValues.getCurrentLocalRelativeValue();
        return baseValue.add(relativeValue);
    }

    static Vector2f getCurrentLocalValue2f(EffectiveDynamicValues<Vector2f> effectiveDynamicValues) {
        Vector2f baseValue = effectiveDynamicValues.getLocalBaseValue();
        Vector2f relativeValue = effectiveDynamicValues.getCurrentLocalRelativeValue();
        return baseValue.add(relativeValue);
    }

    static float getCurrentLocalValue1f(EffectiveDynamicValues<Float> effectiveDynamicValues) {
        Float baseValue = effectiveDynamicValues.getLocalBaseValue();
        Float relativeValue = effectiveDynamicValues.getCurrentLocalRelativeValue();
        return (baseValue + relativeValue);
    }

    static Vector3f cloneIfNotNull(Vector3f value) {
        return ((value != null) ? value.clone() : null);
    }

    static Quaternion cloneIfNotNull(Quaternion value) {
        return ((value != null) ? value.clone() : null);
    }
}
