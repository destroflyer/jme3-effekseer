package com.destroflyer.jme3.effekseer.nativ;

import Effekseer.swig.EffekseerEffectCore;
import Effekseer.swig.EffekseerManagerCore;
import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import com.jme3.renderer.Camera;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EffekseerManager {

    public EffekseerManager() {
        state = new State();
        state.core = new EffekseerManagerCore();
    }
    private State state;

    private static class EmitterState {}
    private static class State {
        private EffekseerManagerCore core;
        private Collection<Spatial> currentSceneParents;
        private final Map<EffekseerControl, EmitterState> emitters = new WeakHashMap<>();
        private WeakReference<Spatial>[] bitLayers = new WeakReference[30];
        private float[] v16 = new float[16];
        private Matrix4f m4 = new Matrix4f();
        private List<Spatial> v1SpatialList = new ArrayList<>(1);
        {
            v1SpatialList.add(null);
        }
        private boolean asyncInit = false;
        private ConcurrentLinkedQueue<List<Integer>> garbagePile = new ConcurrentLinkedQueue<>();
    }

    public void setAsync(int threads) {
        if (!state.asyncInit) {
            state.core.LaunchWorkerThreads(threads);
            state.asyncInit = true;
        }
    }

    public void initialize(boolean sRGB) {
        state.core.Initialize(8000, sRGB);
    }

    public void update(float tpf) {
        while (state.garbagePile.size() != 0) {
            List<Integer> garbage = state.garbagePile.poll();
            for (int i : garbage) {
                stopEffect(i);
            }
        }
        float t = tpf / (1.0f / 60.0f);
        state.core.Update(t);
    }

    private int getLayer(Spatial parent) {
        Integer layerId = parent.getUserData("_effekseer_layer");
        if (layerId == null) {
            for (int i = 0; i < 30; i++) {
                if (state.bitLayers[i] == null || state.bitLayers[i].get() == null) {
                    state.bitLayers[i] = new WeakReference<>(parent);
                    layerId = i + 2;
                    break;
                }
            }
            parent.setUserData("_effekseer_layer",layerId);
        }
        return layerId;
    }

    /**
     * Select a scene for rendering and update. Same as beginRender(Spatial) but accepts multiple parents.
     * @param parents Only effects that are child of the parent spatials (or attached to the parent spatials) will be updated and rendered
     */
    public void beginRender(Collection<Spatial> parents) {
        state.currentSceneParents = parents;
        if (parents == null) {
            return;
        }

        for (Entry<EffekseerControl,EmitterState> e : state.emitters.entrySet()) {
            EffekseerControl emitter = e.getKey();
            Spatial parent = parents.stream().filter(emitter::isChildOf).findAny().orElse(null);
            if (parent != null) {
                int layer = getLayer(parent);
                emitter.setLayer(layer);
            }
        }
    }

    /**
     * This must be called after the rendering, to deselect the scene and reset temporary states.
     */
    public void endRender() {
        state.currentSceneParents = null;
    }

    public void render(
        Renderer renderer,
        Camera cam,
        FrameBuffer renderTarget,
        Texture sceneColor,
        Texture sceneDepth,
        boolean isOrthographic
    ) {
        if (!(renderer instanceof GLRenderer)) {
            throw new RuntimeException("Only GLRenderer supported at this moment");
        }

        GLRenderer gl = (GLRenderer) renderer;
        gl.setFrameBuffer(renderTarget);

        if (isOrthographic) {
            state.core.SetViewProjectionMatrixWithSimpleWindow(cam.getWidth(), cam.getHeight());
            state.core.UnsetDepth();
            state.core.UnsetBackground();
        } else {
            cam.getProjectionMatrix().get(state.v16,true);
            state.core.SetProjectionMatrix(state.v16[0], state.v16[1], state.v16[2], state.v16[3], state.v16[4], state.v16[5], state.v16[6], state.v16[7], state.v16[8], state.v16[9], state.v16[10], state.v16[11], state.v16[12], state.v16[13], state.v16[14], state.v16[15]);

            cam.getProjectionMatrix().get(state.v16,true);

            cam.getViewMatrix().get(state.v16,true);
            state.core.SetCameraMatrix(state.v16[0], state.v16[1], state.v16[2], state.v16[3], state.v16[4], state.v16[5], state.v16[6], state.v16[7], state.v16[8], state.v16[9], state.v16[10], state.v16[11], state.v16[12], state.v16[13], state.v16[14], state.v16[15]);

            if (sceneDepth != null) {
                state.core.SetDepth(sceneDepth.getImage().getId(),sceneDepth.getImage().hasMipmaps());
            } else {
                state.core.UnsetDepth();
            }

            if (sceneColor != null) {
                state.core.SetBackground(sceneColor.getImage().getId(),sceneColor.getImage().hasMipmaps());
            } else {
                state.core.UnsetBackground();
            }
        }

        int layer = 0;
        for (Spatial p : state.currentSceneParents) {
            layer |= 1 << getLayer(p);
        }
        state.core.DrawBack(layer);
        state.core.DrawFront(layer);
    }

    public int playEffect(EffekseerEffectCore e) {
        return state.core.Play(e);
    }

    public void pauseEffect(int e,boolean v) {
        if (state.core.Exists(e)) {
            state.core.SetPaused(e, v);
        }
    }

    public void stopEffect(int e) {
        if (state.core.Exists(e)) {
            state.core.Stop(e);
        }
    }

    public void setEffectVisibility(int e, boolean v) {
        if (state.core.Exists(e)) {
            state.core.SetShown(e, v);
        }
    }

    public boolean isEffectAlive(int e) {
        return state.core.Exists(e);
    }

    public void setEffectLayer(int handle, int layer) {
        if (state.core.Exists(handle)) {
            state.core.SetLayer(handle,layer);
        }
    }

    public void setDynamicInput(int e, int index, float value) {
        if (state.core.Exists(e)) {
            state.core.SetDynamicInput(e, index, value);
        }
    }

    public void setEffectTransform(int handler, Transform transform) {
        if (!state.core.Exists(handler)) {
            return;
        }
        state.m4.setTranslation(transform.getTranslation());
        state.m4.setRotationQuaternion(transform.getRotation());
        state.m4.setScale(transform.getScale());

        state.m4.get(state.v16, true);
        state.core.SetEffectTransformBaseMatrix(handler, state.v16[0], state.v16[1], state.v16[2], state.v16[3], state.v16[4], state.v16[5], state.v16[6], state.v16[7], state.v16[8], state.v16[9], state.v16[10], state.v16[11]);
    }

    public void registerEmitter(EffekseerControl control) {
        if (!state.emitters.containsKey(control)) {
            state.emitters.put(control, new EmitterState());
            control.setGarbagePile(state.garbagePile);
        }
    }

    public void unregisterEmitter(EffekseerControl control) {
        state.emitters.remove(control);
    }

    public void destroy() {
        state.core.delete();
    }
}
