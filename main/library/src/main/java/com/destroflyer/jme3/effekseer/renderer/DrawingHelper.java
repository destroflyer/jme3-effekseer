package com.destroflyer.jme3.effekseer.renderer;

import com.destroflyer.jme3.effekseer.model.*;
import com.jme3.math.Vector2f;

import static com.destroflyer.jme3.effekseer.renderer.ColorHelper.generateEffectiveColorValues;
import static com.destroflyer.jme3.effekseer.renderer.ColorHelper.updateCurrentColorValue;
import static com.destroflyer.jme3.effekseer.renderer.DynamicValuesHelper.generateEffectiveDynamicValues;
import static com.destroflyer.jme3.effekseer.renderer.DynamicValuesHelper.updateCurrentLocalRelativeDynamicValue;

public class DrawingHelper {

    static EffectiveDrawingValues generateEffectiveDrawingValues_Particle(DrawingValues drawingValues) {
        if (drawingValues instanceof RibbonDrawingValues) {
            RibbonDrawingValues ribbonDrawingValues = (RibbonDrawingValues) drawingValues;
            return generateEffectiveDrawingValues(ribbonDrawingValues.getParticleDrawingValues());
        } else {
            return generateEffectiveDrawingValues(drawingValues);
        }
    }

    static EffectiveDrawingValues generateEffectiveDrawingValues(DrawingValues drawingValues) {
        if (drawingValues instanceof SpriteDrawingValues) {
            SpriteDrawingValues spriteDrawingValues = (SpriteDrawingValues) drawingValues;
            EffectiveColorValues effectiveColorValues = generateEffectiveColorValues(spriteDrawingValues.getColorValues());
            return EffectiveSpriteDrawingValues.builder()
                    .effectiveColorValues(effectiveColorValues)
                    .build();
        } else if (drawingValues instanceof TrackDrawingValues) {
            TrackDrawingValues trackDrawingValues = (TrackDrawingValues) drawingValues;
            EffectiveColorValues effectiveColorValuesLeftFixed = generateEffectiveColorValues(trackDrawingValues.getColorValuesLeft());
            EffectiveColorValues effectiveColorValuesLeftCenterFixed = generateEffectiveColorValues(trackDrawingValues.getColorValuesLeftCenter());
            EffectiveColorValues effectiveColorValuesCenterFixed = generateEffectiveColorValues(trackDrawingValues.getColorValuesCenter());
            EffectiveColorValues effectiveColorValuesCenterMiddleFixed = generateEffectiveColorValues(trackDrawingValues.getColorValuesCenterMiddle());
            EffectiveColorValues effectiveColorValuesRightFixed = generateEffectiveColorValues(trackDrawingValues.getColorValuesRight());
            EffectiveColorValues effectiveColorValuesRightCenterFixed = generateEffectiveColorValues(trackDrawingValues.getColorValuesRightCenter());
            return EffectiveTrackDrawingValues.builder()
                    .effectiveColorValuesLeftFixed(effectiveColorValuesLeftFixed)
                    .effectiveColorValuesLeftCenterFixed(effectiveColorValuesLeftCenterFixed)
                    .effectiveColorValuesCenterFixed(effectiveColorValuesCenterFixed)
                    .effectiveColorValuesCenterMiddleFixed(effectiveColorValuesCenterMiddleFixed)
                    .effectiveColorValuesRightFixed(effectiveColorValuesRightFixed)
                    .effectiveColorValuesRightCenterFixed(effectiveColorValuesRightCenterFixed)
                    .build();
        } else if (drawingValues instanceof RingDrawingValues) {
            RingDrawingValues ringDrawingValues = (RingDrawingValues) drawingValues;
            EffectiveDynamicValues<Float> effectiveViewingAngleValues = generateEffectiveDynamicValues(ringDrawingValues.getViewingAngleValues());
            EffectiveDynamicValues<Vector2f> effectiveInnerPositionValues = generateEffectiveDynamicValues(ringDrawingValues.getInnerPositionValues());
            EffectiveDynamicValues<Vector2f> effectiveOuterPositionValues = generateEffectiveDynamicValues(ringDrawingValues.getOuterPositionValues());
            EffectiveDynamicValues<Float> effectiveCenterRatioValues = generateEffectiveDynamicValues(ringDrawingValues.getCenterRatioValues());
            EffectiveColorValues effectiveColorValuesInner = generateEffectiveColorValues(ringDrawingValues.getColorValuesInner());
            EffectiveColorValues effectiveColorValuesCenter = generateEffectiveColorValues(ringDrawingValues.getColorValuesCenter());
            EffectiveColorValues effectiveColorValuesOuter = generateEffectiveColorValues(ringDrawingValues.getColorValuesOuter());
            return EffectiveRingDrawingValues.builder()
                    .effectiveViewingAngleValues(effectiveViewingAngleValues)
                    .effectiveInnerPositionValues(effectiveInnerPositionValues)
                    .effectiveOuterPositionValues(effectiveOuterPositionValues)
                    .effectiveCenterRatioValues(effectiveCenterRatioValues)
                    .effectiveCenterRatioValues(effectiveCenterRatioValues)
                    .effectiveColorValuesInner(effectiveColorValuesInner)
                    .effectiveColorValuesCenter(effectiveColorValuesCenter)
                    .effectiveColorValuesOuter(effectiveColorValuesOuter)
                    .build();
        } else if (drawingValues instanceof RibbonParticleDrawingValues) {
            RibbonParticleDrawingValues ribbonParticleDrawingValues = (RibbonParticleDrawingValues) drawingValues;
            EffectiveColorValues effectiveColorValuesAll = generateEffectiveColorValues(ribbonParticleDrawingValues.getColorValuesAll());
            return EffectiveRibbonParticleDrawingValues.builder()
                    .effectiveColorValuesAll(effectiveColorValuesAll)
                    .build();
        }
        return null;
    }

    static void updateCurrentDrawingValue(Particle particle, EffectiveDrawingValues effectiveDrawingValues, float frameProgress) {
        if (effectiveDrawingValues instanceof EffectiveSpriteDrawingValues) {
            EffectiveSpriteDrawingValues effectiveSpriteDrawingValues = (EffectiveSpriteDrawingValues) effectiveDrawingValues;
            updateCurrentColorValue(particle, effectiveSpriteDrawingValues.getEffectiveColorValues());
        } else if (effectiveDrawingValues instanceof EffectiveTrackDrawingValues) {
            EffectiveTrackDrawingValues effectiveTrackDrawingValues = (EffectiveTrackDrawingValues) effectiveDrawingValues;
            updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesLeftFixed());
            updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesLeftCenterFixed());
            updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesCenterFixed());
            updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesCenterMiddleFixed());
            updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesRightFixed());
            updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesRightCenterFixed());
        } else if (effectiveDrawingValues instanceof EffectiveRingDrawingValues) {
            EffectiveRingDrawingValues effectiveRingDrawingValues = (EffectiveRingDrawingValues) effectiveDrawingValues;
            updateCurrentLocalRelativeDynamicValue(particle, effectiveRingDrawingValues.getEffectiveViewingAngleValues(), frameProgress);
            updateCurrentLocalRelativeDynamicValue(particle, effectiveRingDrawingValues.getEffectiveInnerPositionValues(), frameProgress);
            updateCurrentLocalRelativeDynamicValue(particle, effectiveRingDrawingValues.getEffectiveOuterPositionValues(), frameProgress);
            updateCurrentLocalRelativeDynamicValue(particle, effectiveRingDrawingValues.getEffectiveCenterRatioValues(), frameProgress);
            updateCurrentColorValue(particle, effectiveRingDrawingValues.getEffectiveColorValuesInner());
            updateCurrentColorValue(particle, effectiveRingDrawingValues.getEffectiveColorValuesCenter());
            updateCurrentColorValue(particle, effectiveRingDrawingValues.getEffectiveColorValuesOuter());
        } else if (effectiveDrawingValues instanceof EffectiveRibbonParticleDrawingValues) {
            EffectiveRibbonParticleDrawingValues effectiveRibbonParticleDrawingValues = (EffectiveRibbonParticleDrawingValues) effectiveDrawingValues;
            updateCurrentColorValue(particle, effectiveRibbonParticleDrawingValues.getEffectiveColorValuesAll());
        }
    }
}
