package com.destroflyer.jme3.effekseer.virtual.renderer;

import com.destroflyer.jme3.effekseer.virtual.model.*;
import com.jme3.math.Vector2f;

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
            EffectiveColorValues effectiveColorValues = ColorHelper.generateEffectiveColorValues(spriteDrawingValues.getColorValues());
            return EffectiveSpriteDrawingValues.builder()
                    .effectiveColorValues(effectiveColorValues)
                    .build();
        } else if (drawingValues instanceof TrackDrawingValues) {
            TrackDrawingValues trackDrawingValues = (TrackDrawingValues) drawingValues;
            EffectiveColorValues effectiveColorValuesLeftFixed = ColorHelper.generateEffectiveColorValues(trackDrawingValues.getColorValuesLeft());
            EffectiveColorValues effectiveColorValuesLeftCenterFixed = ColorHelper.generateEffectiveColorValues(trackDrawingValues.getColorValuesLeftCenter());
            EffectiveColorValues effectiveColorValuesCenterFixed = ColorHelper.generateEffectiveColorValues(trackDrawingValues.getColorValuesCenter());
            EffectiveColorValues effectiveColorValuesCenterMiddleFixed = ColorHelper.generateEffectiveColorValues(trackDrawingValues.getColorValuesCenterMiddle());
            EffectiveColorValues effectiveColorValuesRightFixed = ColorHelper.generateEffectiveColorValues(trackDrawingValues.getColorValuesRight());
            EffectiveColorValues effectiveColorValuesRightCenterFixed = ColorHelper.generateEffectiveColorValues(trackDrawingValues.getColorValuesRightCenter());
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
            EffectiveDynamicValues<Float> effectiveViewingAngleValues = DynamicValuesHelper.generateEffectiveDynamicValues(ringDrawingValues.getViewingAngleValues());
            EffectiveDynamicValues<Vector2f> effectiveInnerPositionValues = DynamicValuesHelper.generateEffectiveDynamicValues(ringDrawingValues.getInnerPositionValues());
            EffectiveDynamicValues<Vector2f> effectiveOuterPositionValues = DynamicValuesHelper.generateEffectiveDynamicValues(ringDrawingValues.getOuterPositionValues());
            EffectiveDynamicValues<Float> effectiveCenterRatioValues = DynamicValuesHelper.generateEffectiveDynamicValues(ringDrawingValues.getCenterRatioValues());
            EffectiveColorValues effectiveColorValuesInner = ColorHelper.generateEffectiveColorValues(ringDrawingValues.getColorValuesInner());
            EffectiveColorValues effectiveColorValuesCenter = ColorHelper.generateEffectiveColorValues(ringDrawingValues.getColorValuesCenter());
            EffectiveColorValues effectiveColorValuesOuter = ColorHelper.generateEffectiveColorValues(ringDrawingValues.getColorValuesOuter());
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
            EffectiveColorValues effectiveColorValuesAll = ColorHelper.generateEffectiveColorValues(ribbonParticleDrawingValues.getColorValuesAll());
            return EffectiveRibbonParticleDrawingValues.builder()
                    .effectiveColorValuesAll(effectiveColorValuesAll)
                    .build();
        }
        return null;
    }

    static void updateCurrentDrawingValue(Particle particle, EffectiveDrawingValues effectiveDrawingValues, float frameProgress) {
        if (effectiveDrawingValues instanceof EffectiveSpriteDrawingValues) {
            EffectiveSpriteDrawingValues effectiveSpriteDrawingValues = (EffectiveSpriteDrawingValues) effectiveDrawingValues;
            ColorHelper.updateCurrentColorValue(particle, effectiveSpriteDrawingValues.getEffectiveColorValues());
        } else if (effectiveDrawingValues instanceof EffectiveTrackDrawingValues) {
            EffectiveTrackDrawingValues effectiveTrackDrawingValues = (EffectiveTrackDrawingValues) effectiveDrawingValues;
            ColorHelper.updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesLeftFixed());
            ColorHelper.updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesLeftCenterFixed());
            ColorHelper.updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesCenterFixed());
            ColorHelper.updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesCenterMiddleFixed());
            ColorHelper.updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesRightFixed());
            ColorHelper.updateCurrentColorValue(particle, effectiveTrackDrawingValues.getEffectiveColorValuesRightCenterFixed());
        } else if (effectiveDrawingValues instanceof EffectiveRingDrawingValues) {
            EffectiveRingDrawingValues effectiveRingDrawingValues = (EffectiveRingDrawingValues) effectiveDrawingValues;
            DynamicValuesHelper.updateCurrentLocalRelativeDynamicValue(particle, effectiveRingDrawingValues.getEffectiveViewingAngleValues(), frameProgress);
            DynamicValuesHelper.updateCurrentLocalRelativeDynamicValue(particle, effectiveRingDrawingValues.getEffectiveInnerPositionValues(), frameProgress);
            DynamicValuesHelper.updateCurrentLocalRelativeDynamicValue(particle, effectiveRingDrawingValues.getEffectiveOuterPositionValues(), frameProgress);
            DynamicValuesHelper.updateCurrentLocalRelativeDynamicValue(particle, effectiveRingDrawingValues.getEffectiveCenterRatioValues(), frameProgress);
            ColorHelper.updateCurrentColorValue(particle, effectiveRingDrawingValues.getEffectiveColorValuesInner());
            ColorHelper.updateCurrentColorValue(particle, effectiveRingDrawingValues.getEffectiveColorValuesCenter());
            ColorHelper.updateCurrentColorValue(particle, effectiveRingDrawingValues.getEffectiveColorValuesOuter());
        } else if (effectiveDrawingValues instanceof EffectiveRibbonParticleDrawingValues) {
            EffectiveRibbonParticleDrawingValues effectiveRibbonParticleDrawingValues = (EffectiveRibbonParticleDrawingValues) effectiveDrawingValues;
            ColorHelper.updateCurrentColorValue(particle, effectiveRibbonParticleDrawingValues.getEffectiveColorValuesAll());
        }
    }
}
