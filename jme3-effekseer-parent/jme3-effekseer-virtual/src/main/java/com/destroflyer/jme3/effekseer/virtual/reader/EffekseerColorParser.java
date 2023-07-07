package com.destroflyer.jme3.effekseer.virtual.reader;

import com.destroflyer.jme3.effekseer.virtual.model.*;
import com.jme3.math.ColorRGBA;
import org.jdom2.Element;

public class EffekseerColorParser {

    static ColorValues parseColorValues(Element parentElement, String elementNameBase, int defaultAlpha) {
        int type = EffekseerBasics.parseInteger(parentElement, elementNameBase, 0);
        switch (type) {
            case 0:
                Element fixed = ((parentElement != null) ? parentElement.getChild(elementNameBase + "_Fixed") : null);
                return parseColorValues_Fixed(fixed, defaultAlpha);
            case 1:
                Element random = parentElement.getChild(elementNameBase + "_Random");
                return parseColorValues_Random(random, defaultAlpha);
            case 2:
                Element easing = parentElement.getChild(elementNameBase + "_Easing");
                return parseColorValues_Easing(easing);
            case 3:
                Element fCurve = parentElement.getChild(elementNameBase + "_FCurve");
                // TODO: F-Curve
                return FCurveColorValues.builder()
                        .dummy(ColorRGBA.White)
                        .build();
        }
        return null;
    }

    static FixedColorValues parseColorValues_Fixed(Element fixed, int defaultAlpha) {
        ColorRGBA base = EffekseerBasics.parseColor(fixed, defaultAlpha);

        return FixedColorValues.builder()
                .base(base)
                .build();
    }

    static RandomColorValues parseColorValues_Random(Element random, int defaultAlpha) {
        ColorRange base = EffekseerHelper.parseColorRange(random, defaultAlpha);

        return RandomColorValues.builder()
                .base(base)
                .build();
    }

    static EasingColorValues parseColorValues_Easing(Element easing) {
        ColorRange start = EffekseerHelper.parseColorRange(easing, "Start", 255);
        ColorRange end = EffekseerHelper.parseColorRange(easing, "End", 255);

        return EasingColorValues.builder()
                .start(start)
                .end(end)
                .build();
    }
}
