package com.destroflyer.jme3.effekseer.virtual.renderer;

import com.destroflyer.jme3.effekseer.virtual.model.*;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class RangeValues {

    static Object generateRangeValue(Range range) {
        if (range instanceof Range3f) {
            Range3f range3f = (Range3f) range;
            return generateRangeValue3f(range3f);
        } else if (range instanceof Range2f) {
            Range2f range2f = (Range2f) range;
            return generateRangeValue2f(range2f);
        } else if (range instanceof Range1f) {
            Range1f range1f = (Range1f) range;
            return generateRangeValue1f(range1f);
        }
        return null;
    }

    static Object generateSingleRangeValue(Class<? extends Range> rangeClass, Range1f range1f) {
        float rangeValue = generateRangeValue1f(range1f);
        if (rangeClass == Range3f.class) {
            return new Vector3f(rangeValue, rangeValue, rangeValue);
        } else if (rangeClass == Range2f.class) {
            return new Vector2f(rangeValue, rangeValue);
        } else if (rangeClass == Range1f.class) {
            return rangeValue;
        }
        return null;
    }

    static Vector3f generateRangeValue3f(Range3f range3F) {
        float x = generateRangeValue1f(range3F.getX());
        float y = generateRangeValue1f(range3F.getY());
        float z = generateRangeValue1f(range3F.getZ());
        return new Vector3f(x, y, z);
    }

    static Vector2f generateRangeValue2f(Range2f range2F) {
        float x = generateRangeValue1f(range2F.getX());
        float y = generateRangeValue1f(range2F.getY());
        return new Vector2f(x, y);
    }

    static ColorRGBA generateColorRangeValue(ColorRange colorRange) {
        float red = generateRangeValue1f(colorRange.getRed());
        float green = generateRangeValue1f(colorRange.getGreen());
        float blue = generateRangeValue1f(colorRange.getBlue());
        float alpha = generateRangeValue1f(colorRange.getAlpha());
        if (colorRange.getColorSpace() == ColorSpace.HSV) {
            // TODO: Convert from HSV to RGB (java.awt.Color only has HSB instead of HSV)
        }
        return new ColorRGBA(red, green, blue, alpha);
    }

    static float generateRangeValue1f(Range1f range) {
        return (range.getMin() + (FastMath.rand.nextFloat() * (range.getMax() - range.getMin())));
    }
}
