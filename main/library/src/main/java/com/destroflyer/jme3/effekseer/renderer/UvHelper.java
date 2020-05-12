package com.destroflyer.jme3.effekseer.renderer;

import com.destroflyer.jme3.effekseer.model.FixedUvValues;
import com.destroflyer.jme3.effekseer.model.ScrollUvValues;
import com.destroflyer.jme3.effekseer.model.StandardUvValues;
import com.destroflyer.jme3.effekseer.model.UvValues;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;

import static com.destroflyer.jme3.effekseer.renderer.RangeValues.generateRangeValue2f;

public class UvHelper {

    static EffectiveUvValues generateEffectiveUvValues(UvValues uvValues) {
        if (uvValues instanceof StandardUvValues) {
            return new EffectiveStandardUvValues();
        } else if (uvValues instanceof FixedUvValues) {
            FixedUvValues fixedUvValues = (FixedUvValues) uvValues;
            return EffectiveFixedUvValues.builder()
                    .start(fixedUvValues.getStart())
                    .size(fixedUvValues.getSize())
                    .build();
        } else if (uvValues instanceof ScrollUvValues) {
            ScrollUvValues scrollUvValues = (ScrollUvValues) uvValues;
            Vector2f start = generateRangeValue2f(scrollUvValues.getStart());
            Vector2f size = generateRangeValue2f(scrollUvValues.getSize());
            Vector2f speed = generateRangeValue2f(scrollUvValues.getSpeed());
            return EffectiveScrollUvValues.builder()
                    .start(start)
                    .size(size)
                    .speed(speed)
                    .build();
        }
        return null;
    }

    static float[] getUV_XY(float[] pixelPositions, Texture texture) {
        float[] uvPositions = new float[pixelPositions.length];
        for (int i = 0; i < pixelPositions.length; i += 2) {
            uvPositions[i] = getUV_X(pixelPositions[i], texture);
            uvPositions[i + 1] = getUV_Y(pixelPositions[i + 1], texture);
        }
        return uvPositions;
    }

    static float getUV_X(float pixelPosition, Texture texture) {
        return getUV(pixelPosition, texture.getImage().getWidth());
    }

    static float getUV_Y(float pixelPosition, Texture texture) {
        return getUV(pixelPosition, texture.getImage().getHeight());
    }

    private static float getUV(float pixelPosition, int textureSize) {
        return (pixelPosition / textureSize);
    }
}
