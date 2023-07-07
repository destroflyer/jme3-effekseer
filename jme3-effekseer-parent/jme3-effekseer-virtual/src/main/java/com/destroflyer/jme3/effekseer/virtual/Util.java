package com.destroflyer.jme3.effekseer.virtual;

import java.util.Collection;
import java.util.Iterator;

public class Util {

    public static short[] convertToArray_Short(Collection<Short> collection) {
        short[] array = new short[collection.size()];
        Iterator<Short> iterator = collection.iterator();
        for (int i=0;i<array.length;i++) {
            array[i] = iterator.next();
        }
        return array;
    }
    
    public static float[] convertToArray_Float(Collection<Float> collection) {
        float[] array = new float[collection.size()];
        Iterator<Float> iterator = collection.iterator();
        for (int i=0;i<array.length;i++) {
            array[i] = iterator.next();
        }
        return array;
    }
}
