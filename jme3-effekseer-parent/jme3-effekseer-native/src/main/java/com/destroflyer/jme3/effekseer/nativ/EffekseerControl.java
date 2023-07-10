package com.destroflyer.jme3.effekseer.nativ;

import Effekseer.swig.EffekseerEffectCore;
import com.destroflyer.jme3.effekseer.nativ.driver.EffekseerEmissionDriverGeneric;
import com.destroflyer.jme3.effekseer.nativ.driver.fun.EffekseerEmissionDriver;
import com.jme3.asset.AssetManager;
import com.jme3.math.Transform;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EffekseerControl extends AbstractControl {

    public EffekseerControl(AssetManager assetManager, String path) {
        this(assetManager.loadAsset(new EffekseerEffectKey(path)));
    }

    public EffekseerControl(EffekseerEffectCore effect) {
        this.effect = effect;
        manager = new EffekseerManager();
    }
    private ArrayList<Integer> instances = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private EffekseerManager manager;
    private EffekseerEffectCore effect;
    private boolean play = true;
    @Setter
    private float speed = 1;
    private int layer = 0;
    private EffekseerEmissionDriver driver = new EffekseerEmissionDriverGeneric();
    @Setter(AccessLevel.PACKAGE)
    private ConcurrentLinkedQueue<List<Integer>> garbagePile;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial == null) {
            stop();
        } else {
            manager.registerEmitter(this);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (int i : instances) {
            manager.pauseEffect(i, !enabled);
            manager.setEffectVisibility(i, enabled);
        }
    }

    public boolean isPlaying() {
        return instances.size() > 0;
    }

    public void play() {
        if (play) {
            return;
        }
        instances.forEach(i -> manager.pauseEffect(i, false));
        play = true;
    }

    public void pause() {
        if (!play) {
            return;
        }
        instances.forEach(i -> manager.pauseEffect(i, true));
        play = false;
    }

    public void stop() {
        if (!play) {
            return;
        }
        instances.forEach(manager::stopEffect);
        instances.clear();
        play = false;
    }

    boolean isChildOf(Spatial parent) {
        return isChildOf(spatial,parent);
    }

    private boolean isChildOf(Spatial spatial, Spatial parent) {
        if (spatial == null) {
            return false;
        } else if (spatial == parent) {
            return true;
        } else {
            Spatial nextS = spatial.getParent();
            if (nextS == null) {
                return false;
            }
            return isChildOf(nextS, parent);
        }
    }

    void setLayer(int layer) {
        this.layer = layer;
        instances.forEach(i -> manager.setEffectLayer(i, this.layer));
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (!play) {
            return;
        }

        manager.ensureInitialized(Effekseer.IS_SRGB);

        Integer newHandle = driver.tryEmit(() -> manager.playEffect(effect));

        if (newHandle != null) {
            instances.add(newHandle);
            manager.setEffectLayer(newHandle, layer);
        }  

        for (int i = 0; i < instances.size(); i++) {
            Integer handle = instances.get(i);
            if (!manager.isEffectAlive(handle)) {
                driver.destroy(handle);
                instances.remove(i);
                i--;
            } else {
                driver.setDynamicInputs(handle, (index,value) -> manager.setDynamicInput(handle, index, value));
                Transform transform = driver.getInstanceTransform(handle, spatial);
                manager.setEffectTransform(handle, transform);
            }
        }

        float adjustedTpf = tpf * speed;
        driver.update(adjustedTpf);
        manager.update(adjustedTpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    @Override
    public void finalize() {        
        garbagePile.add(instances);
    }
}