package com.destroflyer.jme3.effekseer.renderer;

import com.destroflyer.jme3.effekseer.model.*;
import com.jme3.math.ColorRGBA;

import static com.destroflyer.jme3.effekseer.renderer.RangeValues.generateColorRangeValue;

public class ColorHelper {

    static EffectiveColorValues generateEffectiveColorValues(ColorValues colorValues) {
        EffectiveColorValues effectiveColorValues = null;
        ColorRGBA startValue = null;
        if (colorValues instanceof FixedColorValues) {
            FixedColorValues fixedColorValues = (FixedColorValues) colorValues;
            ColorRGBA base = fixedColorValues.getBase();
            startValue = base;
            effectiveColorValues = EffectiveFixedColorValues.builder()
                    .base(base)
                    .build();
        } else if (colorValues instanceof EasingColorValues) {
            EasingColorValues easingColorValues = (EasingColorValues) colorValues;
            ColorRGBA start = generateColorRangeValue(easingColorValues.getStart());
            ColorRGBA end = generateColorRangeValue(easingColorValues.getEnd());
            startValue = start;
            effectiveColorValues = EffectiveEasingColorValues.builder()
                    .start(start)
                    .end(end)
                    .build();
        } else if (colorValues instanceof RandomColorValues) {
            RandomColorValues randomColorValues = (RandomColorValues) colorValues;
            ColorRGBA base = generateColorRangeValue(randomColorValues.getBase());
            startValue = base;
            effectiveColorValues = EffectiveFixedColorValues.builder()
                    .base(base)
                    .build();
        } else if (colorValues instanceof FCurveColorValues) {
            FCurveColorValues fCurveColorValues = (FCurveColorValues) colorValues;
            ColorRGBA dummy = fCurveColorValues.getDummy();
            startValue = dummy;
            effectiveColorValues = EffectiveFCurveColorValues.builder()
                    .dummy(dummy)
                    .build();
        }
        effectiveColorValues.setCurrentValue(startValue);
        return effectiveColorValues;
    }

    static void updateCurrentColorValue(Particle particle, EffectiveColorValues effectiveColorValues) {
        if (effectiveColorValues instanceof EffectiveEasingColorValues) {
            EffectiveEasingColorValues effectiveEasingColorValues = (EffectiveEasingColorValues) effectiveColorValues;
            ColorRGBA start = effectiveEasingColorValues.getStart();
            ColorRGBA end = effectiveEasingColorValues.getEnd();
            float progress = (1 - (particle.getRemainingLife() / particle.getStartingLife()));
            ColorRGBA newValue = new ColorRGBA(start).interpolateLocal(end, progress);
            effectiveColorValues.setCurrentValue(newValue);
        }
    }
}
