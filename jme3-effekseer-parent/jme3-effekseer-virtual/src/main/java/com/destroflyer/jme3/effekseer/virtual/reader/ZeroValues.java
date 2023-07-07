package com.destroflyer.jme3.effekseer.virtual.reader;

import com.destroflyer.jme3.effekseer.virtual.model.*;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class ZeroValues {

    public static Object getZeroValueByValueClass(Class<?> valueClass) {
        if (valueClass == Vector3f.class) {
            return Vector3f.ZERO;
        } else if (valueClass == Vector2f.class) {
            return Vector2f.ZERO;
        } else if (valueClass == Float.class) {
            return 0f;
        }
        return null;
    }

    public static Object getZeroValueByRangeClass(Class<? extends Range> rangeClass) {
        if (rangeClass == Range3f.class) {
            return Vector3f.ZERO;
        } else if (rangeClass == Range2f.class) {
            return Vector2f.ZERO;
        } else if (rangeClass == Range1f.class) {
            return 0f;
        }
        return null;
    }
}
