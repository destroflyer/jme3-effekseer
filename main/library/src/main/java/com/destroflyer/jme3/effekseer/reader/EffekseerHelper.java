package com.destroflyer.jme3.effekseer.reader;

import com.destroflyer.jme3.effekseer.model.*;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import org.jdom2.Element;

import static com.destroflyer.jme3.effekseer.reader.EffekseerBasics.*;
import static com.destroflyer.jme3.effekseer.reader.ZeroValues.getZeroValueByRangeClass;

public class EffekseerHelper {

    static DynamicValues parseDynamicValues_Nested(Element element, String elementNameBase, Object defaultValue, Class<? extends Range> rangeClass) {
        if (element == null) {
            return FixedDynamicValues.builder()
                    .base(createFixedRange(defaultValue))
                    .build();
        }
        int type = parseInteger(element.getChild("Type"), 0);
        switch (type) {
            case 0:
                Element fixed = element.getChild("Fixed");
                return parseDynamicValues_Fixed(fixed, elementNameBase, defaultValue, rangeClass);
            case 1:
                Element pva = element.getChild("PVA");
                return parseDynamicValues_Pva(pva, elementNameBase, defaultValue, rangeClass);
            case 2:
                Element easing = element.getChild("Easing");
                return parseDynamicValues_Easing(easing, defaultValue, rangeClass);
            case 3:
                switch (elementNameBase) {
                    case "Location":
                        Element fCurve = element.getChild("FCurve");
                        // TODO: F-Curve
                        return FCurveDynamicValues.builder()
                                .dummy(getZeroValueByRangeClass(rangeClass))
                                .build();
                    case "Rotation":
                        // TODO: PVA (Arbitrary Axis)
                        return FCurveDynamicValues.builder()
                                .dummy(getZeroValueByRangeClass(rangeClass))
                                .build();
                    case "Scale":
                        Element singlePva = element.getChild("SinglePVA");
                        return parseDynamicValues_SinglePva(singlePva, elementNameBase, defaultValue, rangeClass);
                    default:
                        return null;
                }
            case 4:
                switch (elementNameBase) {
                    case "Rotation":
                        // TODO: Easing (Arbitrary Axis)
                        return FCurveDynamicValues.builder()
                                .dummy(getZeroValueByRangeClass(rangeClass))
                                .build();
                    case "Scale":
                        Element singleEasing = element.getChild("SingleEasing");
                        return parseDynamicValues_SingleEasing(singleEasing, defaultValue, rangeClass);
                    default:
                        return null;
                }
            case 5:
                switch (elementNameBase) {
                    case "Rotation":
                    case "Scale":
                        Element fCurve = element.getChild("FCurve");
                        // TODO: F-Curve
                        return FCurveDynamicValues.builder()
                                .dummy(getZeroValueByRangeClass(rangeClass))
                                .build();
                    default:
                        return null;
                }
        }
        return null;
    }

    static DynamicValues parseDynamicValues_Flat(Element parentElement, String name, String elementNameBase, Object defaultValue, Class<? extends Range> rangeClass) {
        int type = parseInteger(parentElement, name, 0);
        switch (type) {
            case 0:
                Element fixed = parentElement.getChild(name + "_Fixed");
                return parseDynamicValues_Fixed(fixed, elementNameBase, defaultValue, rangeClass);
            case 1:
                if (rangeClass == Range1f.class) {
                    Element random = parentElement.getChild(name + "_Random");
                    return parseDynamicValues_Random(random, rangeClass);
                } else {
                    Element pva = parentElement.getChild(name + "_PVA");
                    return parseDynamicValues_Pva(pva, elementNameBase, defaultValue, rangeClass);
                }
            case 2:
                Element easing = parentElement.getChild(name + "_Easing");
                Object defaultEasingValue = ((rangeClass == Range2f.class) ? getZeroValueByRangeClass(rangeClass) : defaultValue);
                return parseDynamicValues_Easing(easing, defaultEasingValue, rangeClass);
            case 3:
                Element singlePva = parentElement.getChild(name + "_SinglePVA");
                return parseDynamicValues_SinglePva(singlePva, elementNameBase, defaultValue, rangeClass);
            case 4:
                Element singleEasing = parentElement.getChild(name + "_SingleEasing");
                return parseDynamicValues_SingleEasing(singleEasing, defaultValue, rangeClass);
        }
        return null;
    }

    private static FixedDynamicValues parseDynamicValues_Fixed(Element fixed, String elementNameBase, Object defaultValue, Class<? extends Range> rangeClass) {
        Element baseElement = null;
        if (fixed != null) {
            baseElement = ((elementNameBase != null) ? fixed.getChild(elementNameBase) : fixed);
        }
        Range base0 = parseFixedRange(baseElement, defaultValue, rangeClass);
        return FixedDynamicValues.builder()
                .base(base0)
                .build();
    }

    private static PvaDynamicValues parseDynamicValues_Pva(Element pva, String elementNameBase, Object defaultValue, Class<? extends Range> rangeClass) {
        Range base = parseRange(pva, elementNameBase, defaultValue, rangeClass);
        Range velocity = parseRange(pva, "Velocity", getZeroValueByRangeClass(rangeClass), rangeClass);
        Range acceleration = parseRange(pva, "Acceleration", getZeroValueByRangeClass(rangeClass), rangeClass);
        return PvaDynamicValues.builder()
                .base(base)
                .velocity(velocity)
                .acceleration(acceleration)
                .build();
    }

    private static EasingDynamicValues parseDynamicValues_Easing(Element easing, Object defaultValue, Class<? extends Range> rangeClass) {
        Range start = parseRange(easing, "Start", defaultValue, rangeClass);
        Range end = parseRange(easing, "End", defaultValue, rangeClass);
        return EasingDynamicValues.builder()
                .start(start)
                .end(end)
                .build();
    }

    private static SinglePvaDynamicValues parseDynamicValues_SinglePva(Element singlePva, String elementNameBase, Object defaultValue, Class<? extends Range> rangeClass) {
        Range1f base = parseRange1f(singlePva, elementNameBase, getSingleDefaultValue(defaultValue));
        Range1f velocity = parseRange1f(singlePva, "Velocity", 0);
        Range1f acceleration = parseRange1f(singlePva, "Acceleration", 0);
        return SinglePvaDynamicValues.builder()
                .rangeClass(rangeClass)
                .base(base)
                .velocity(velocity)
                .acceleration(acceleration)
                .build();
    }

    private static SingleEasingDynamicValues parseDynamicValues_SingleEasing(Element singleEasing, Object defaultValue, Class<? extends Range> rangeClass) {
        float singleDefaultValue = getSingleDefaultValue(defaultValue);
        Range1f start = parseRange1f(singleEasing, "Start", singleDefaultValue);
        Range1f end = parseRange1f(singleEasing, "End", singleDefaultValue);
        return SingleEasingDynamicValues.builder()
                .rangeClass(rangeClass)
                .start(start)
                .end(end)
                .build();
    }

    private static RandomDynamicValues parseDynamicValues_Random(Element random, Object defaultValue) {
        Range1f base = parseRange1f(random, getSingleDefaultValue(defaultValue));
        return RandomDynamicValues.builder()
                .base(base)
                .build();
    }

    private static float getSingleDefaultValue(Object defaultValue) {
        if (defaultValue instanceof Vector3f) {
            Vector3f vector3f = (Vector3f) defaultValue;
            return vector3f.getX();
        } else if (defaultValue instanceof Vector2f) {
            Vector2f vector2f = (Vector2f) defaultValue;
            return vector2f.getX();
        } else if (defaultValue instanceof Float) {
            return (float) defaultValue;
        }
        return 0;
    }

    private static Range parseFixedRange(Element baseElement, Object defaultValue, Class<? extends Range> rangeClass) {
        if (rangeClass == Range3f.class) {
            return parseFixedRange3f(baseElement, (Vector3f) defaultValue);
        } else if (rangeClass == Range2f.class) {
            return parseFixedRange2f(baseElement, (Vector2f) defaultValue);
        } else if (rangeClass == Range1f.class) {
            return parseFixedRange1f(baseElement, (float) defaultValue);
        }
        return null;
    }

    private static Range3f parseFixedRange3f(Element baseElement, Vector3f defaultValue) {
        float x = parseFloat(baseElement, "X", defaultValue.getX());
        float y = parseFloat(baseElement, "Y", defaultValue.getY());
        float z = parseFloat(baseElement, "Z", defaultValue.getZ());
        return Range3f.builder()
                .x(createFixedRange1f(x))
                .y(createFixedRange1f(y))
                .z(createFixedRange1f(z))
                .build();
    }

    private static Range2f parseFixedRange2f(Element baseElement, Vector2f defaultValue) {
        float x = parseFloat(baseElement, "X", defaultValue.getX());
        float y = parseFloat(baseElement, "Y", defaultValue.getY());
        return Range2f.builder()
                .x(createFixedRange1f(x))
                .y(createFixedRange1f(y))
                .build();
    }

    private static Range1f parseFixedRange1f(Element baseElement, float defaultValue) {
        float value = parseFloat(baseElement, defaultValue);
        return createFixedRange1f(value);
    }

    private static Range parseRange(Element parentElement, String elementName, Object defaultValue, Class<? extends Range> rangeClass) {
        if (rangeClass == Range3f.class) {
            return parseRange3f(parentElement, elementName, (Vector3f) defaultValue);
        } else if (rangeClass == Range2f.class) {
            return parseRange2f(parentElement, elementName, (Vector2f) defaultValue);
        } else if (rangeClass == Range1f.class) {
            return parseRange1f(parentElement, elementName, (float) defaultValue);
        }
        return null;
    }

    private static Range3f parseRange3f(Element parentElement, String elementName, Vector3f defaultValue) {
        if ((parentElement == null) || (parentElement.getChild(elementName) ==  null)) {
            return (Range3f) createFixedRange(defaultValue);
        }
        Element element = parentElement.getChild(elementName);
        Range1f x = parseRange1f(element.getChild("X"), defaultValue.getX());
        Range1f y = parseRange1f(element.getChild("Y"), defaultValue.getY());
        Range1f z = parseRange1f(element.getChild("Z"), defaultValue.getZ());

        return Range3f.builder()
                .x(x)
                .y(y)
                .z(z)
                .build();
    }

    static Range2f parseRange2f(Element parentElement, String elementName, Vector2f defaultValue) {
        if ((parentElement == null) || (parentElement.getChild(elementName) ==  null)) {
            return (Range2f) createFixedRange(defaultValue);
        }
        Element element = parentElement.getChild(elementName);
        Range1f x = parseRange1f(element.getChild("X"), defaultValue.getX());
        Range1f y = parseRange1f(element.getChild("Y"), defaultValue.getY());

        return Range2f.builder()
                .x(x)
                .y(y)
                .build();
    }

    private static Range createFixedRange(Object value) {
        if (value instanceof Vector3f) {
            return createFixedRange3f((Vector3f) value);
        } else if (value instanceof Vector2f) {
            return createFixedRange2f((Vector2f) value);
        } else if (value instanceof Float) {
            return createFixedRange1f((float) value);
        }
        return null;
    }

    private static Range3f createFixedRange3f(Vector3f value) {
        Range1f x = createFixedRange1f(value.getX());
        Range1f y = createFixedRange1f(value.getX());
        Range1f z = createFixedRange1f(value.getZ());
        return Range3f.builder()
                .x(x)
                .y(y)
                .z(z)
                .build();
    }

    private static Range2f createFixedRange2f(Vector2f value) {
        Range1f x = createFixedRange1f(value.getX());
        Range1f y = createFixedRange1f(value.getX());
        return Range2f.builder()
                .x(x)
                .y(y)
                .build();
    }

    static ColorRange parseColorRange(Element parentElement, String elementName, int defaultAlpha) {
        Element element = ((parentElement != null) ? parentElement.getChild(elementName) : null);
        return parseColorRange(element, defaultAlpha);
    }

    static ColorRange parseColorRange(Element element,int defaultAlpha) {
        if (element == null) {
            return createFixedColorRange(1, defaultAlpha);
        }
        ColorSpace colorSpace = parseEnum(element.getChild("ColorSpace"), ColorSpace.class, ColorSpace.RGB);
        Range1f red = parseColorComponentRange(element.getChild("R"), 255);
        Range1f green = parseColorComponentRange(element.getChild("G"), 255);
        Range1f blue = parseColorComponentRange(element.getChild("B"), 255);
        Range1f alpha = parseColorComponentRange(element.getChild("A"), defaultAlpha);

        return ColorRange.builder()
                .colorSpace(colorSpace)
                .red(red)
                .green(green)
                .blue(blue)
                .alpha(alpha)
                .build();
    }

    private static ColorRange createFixedColorRange(float value, int defaultAlpha) {
        Range1f redGreenBlueRange1f = createFixedRange1f(value);
        Range1f alphaRange1 = createFixedRange1f(convertColorValue(defaultAlpha));
        return ColorRange.builder()
                .red(redGreenBlueRange1f)
                .green(redGreenBlueRange1f)
                .blue(redGreenBlueRange1f)
                .alpha(alphaRange1)
                .build();
    }

    private static Range1f parseColorComponentRange(Element element, int defaultValue) {
        Range1f integerRange = parseRange1f(element, defaultValue);
        float min = convertColorValue((int) integerRange.getMin());
        float max = convertColorValue((int) integerRange.getMax());
        float center = convertColorValue((int) integerRange.getCenter());

        return Range1f.builder()
                .min(min)
                .max(max)
                .center(center)
                .build();
    }
}
