package com.destroflyer.jme3.effekseer.nativ.driver.fun.impl;

import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerEmissionUpdateListener;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.util.clone.Cloner;

import java.io.IOException;
import java.util.Set;
import java.util.function.BiConsumer;

public class EffekseerFunctionalEmissionUpdateListener implements EffekseerEmissionUpdateListener, Cloneable {

    public EffekseerFunctionalEmissionUpdateListener(BiConsumer<Float,Set<Integer>> onUpdateFun) {
        this.onUpdateFun = onUpdateFun;
    }
    private final BiConsumer<Float,Set<Integer>> onUpdateFun;

    @Override
    public void write(JmeExporter ex) throws IOException {

    }

    @Override
    public void read(JmeImporter im) throws IOException {

    }

    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
           return null;
        }
    }

    @Override
    public void cloneFields(Cloner cloner, Object original) {

    }

    @Override
    public void onUpdate(float tpf, Set<Integer> instanceKeysRO) {
        onUpdateFun.accept(tpf,instanceKeysRO);
    }
}
