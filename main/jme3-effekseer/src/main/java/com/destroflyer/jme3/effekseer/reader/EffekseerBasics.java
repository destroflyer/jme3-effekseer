package com.destroflyer.jme3.effekseer.reader;

import com.destroflyer.jme3.effekseer.model.Range1f;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import org.jdom2.Element;

public class EffekseerBasics {

    private static final String VALUE_TRUE = "True";

    static Range1f parseRange1f(Element parentElement, String elementName, float defaultValue) {
        Element element = ((parentElement != null) ? parentElement.getChild(elementName) : null);
        return parseRange1f(element, defaultValue);
    }

    static Range1f parseRange1f(Element element, float defaultValue) {
        if (element == null) {
            return createFixedRange1f(defaultValue);
        }
        float min = parseFloat(element.getChild("Min"), defaultValue);
        float max = parseFloat(element.getChild("Max"), defaultValue);
        float center = parseFloat(element.getChild("Center"), defaultValue);

        return Range1f.builder()
                .min(min)
                .max(max)
                .center(center)
                .build();
    }

    static Range1f createFixedRange1f(float value) {
        return Range1f.builder()
                .min(value)
                .max(value)
                .center(value)
                .build();
    }

    static Vector2f parseVector2f(Element parentElement, String elementName, Vector2f defaultValue) {
        Element element = ((parentElement != null) ? parentElement.getChild(elementName) : null);
        return parseVector2f(element, defaultValue);
    }

    private static Vector2f parseVector2f(Element element, Vector2f defaultValue) {
        float x = parseFloat(element, "X", defaultValue.getX());
        float y = parseFloat(element, "Y", defaultValue.getY());
        return new Vector2f(x, y);
    }

    static float parseFloat(Element parentElement, String elementName, float defaultValue) {
        if (parentElement == null) {
            return defaultValue;
        }
        return parseFloat(parentElement.getChild(elementName), defaultValue);
    }

    static float parseFloat(Element element, float defaultValue) {
        return ((element != null) ? Float.parseFloat(element.getText()) : defaultValue);
    }

    static int parseInteger(Element parentElement, String elementName, int defaultValue) {
        if (parentElement == null) {
            return defaultValue;
        }
        return parseInteger(parentElement.getChild(elementName), defaultValue);
    }

    static int parseInteger(Element element, int defaultValue) {
        return ((element != null) ? Integer.parseInt(element.getText()) : defaultValue);
    }

    static boolean parseBoolean(Element parentElement, String elementName, boolean defaultValue) {
        if (parentElement == null) {
            return defaultValue;
        }
        return parseBoolean(parentElement.getChild(elementName), defaultValue);
    }

    static boolean parseBoolean(Element element, boolean defaultValue) {
        return ((element != null) ? VALUE_TRUE.equals(element.getText()) : defaultValue);
    }

    static <T extends Enum> T parseEnum(Element element, Class<T> enumClass, T defaultValue) {
        return enumClass.getEnumConstants()[parseInteger(element, defaultValue.ordinal())];
    }

    static ColorRGBA parseColor(Element element, int defaultAlpha) {
        float red = parseColorComponent(element, "R", 255);
        float green = parseColorComponent(element,"G", 255);
        float blue = parseColorComponent(element, "B", 255);
        float alpha = parseColorComponent(element, "A", defaultAlpha);

        return new ColorRGBA(red, green, blue, alpha);
    }

    private static float parseColorComponent(Element parentElement, String elementName, int defaultValue) {
        int colorValue = parseInteger(parentElement, elementName, defaultValue);
        return convertColorValue(colorValue);
    }

    static float convertColorValue(int colorValue) {
        return (colorValue / 255f);
    }
}
