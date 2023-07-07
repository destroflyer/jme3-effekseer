package com.destroflyer.jme3.effekseer.nativ;

import Effekseer.swig.EffekseerBackendCore;
import Effekseer.swig.EffekseerEffectCore;
import Effekseer.swig.EffekseerManagerCore;
import Effekseer.swig.EffekseerTextureType;
import com.jme3.asset.AssetLoadException;
import com.jme3.asset.AssetManager;
import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import com.jme3.renderer.Camera;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.scene.Spatial;
import com.jme3.system.NativeLibraryLoader;
import com.jme3.system.Platform;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Effekseer wrapper. This should not be used directly, unless you have to. EffekseerPostRenderer should be used for general usage.
 * Note: 
 *      This implementation uses only one instance of Effekseer runtime per thread. 
 *      All the methods in this class will create and use the instance local to the caller thread and they should 
 *      always be called from the same thread, generally from jME main thread.
 * @author Riccardo Balbo
 */
public class Effekseer {

    static {
        NativeLibraryLoader.registerNativeLibrary("effekseer", Platform.Linux64, "native/linux/x86_64/libEffekseerNativeForJava.so");
        NativeLibraryLoader.registerNativeLibrary("effekseer", Platform.Windows64, "native/windows/x86_64/EffekseerNativeForJava.dll");
        NativeLibraryLoader.loadNativeLibrary("effekseer", true);
    }

    private static class EmitterState {
        boolean oldVisibleFlag;
    }

    private static class State {
        private EffekseerManagerCore core;
        private Collection<Spatial> currentSceneParents;
        private AssetManager am;
        private final Map<EffekseerEmitterControl,EmitterState> emitters= new WeakHashMap<>();

        private WeakReference<Spatial>[] bitLayers = new WeakReference[30];

        private final float[] v16 = new float[16];
        private final Matrix4f m4 = new Matrix4f();
        private final List<Spatial> v1SpatialList = new ArrayList<>(1);
        {
            v1SpatialList.add(null);
        }

        private boolean asyncInit = false;
        private boolean isNew = true;
        
        private final ConcurrentLinkedQueue<List<Integer>> garbagePile= new ConcurrentLinkedQueue<>();
    }

    private static ThreadLocal<State> state= ThreadLocal.withInitial(() -> {
        EffekseerBackendCore.InitializeAsOpenGL();
        State state = new State();
        state.core = new EffekseerManagerCore();
        return state;
    });

    private static State getState() {
        return getState(null);
    }

    private static State getState(Boolean sRGB) {
        State s = state.get();
        if (s.isNew && sRGB != null) {
            s.isNew = false;
            s.core.Initialize(8000,sRGB);
        }
        assert !s.isNew;
        return s;
    }

    public static void setAsync(int threads) {
        State state = getState();
        if (!state.asyncInit) {
            state.core.LaunchWorkerThreads(threads);
            state.asyncInit = true;
        }
    }

    /**
     * Init and return an effekseer instance
     */
    public static State init(AssetManager am,boolean sRGB) {
        State state = getState(sRGB);
        if (state.am != am) {
            if (state.am != null) {
                state.am.unregisterLoader(EffekseerLoader.class);
            }
            state.am = am;
            am.registerLoader(EffekseerLoader.class,"efkefc");
        }
        return state;
    }

    /**
     * Destroy the effekseer instance
     */
    public static void destroy() {
        State s = getState();
        if (s.am != null) {
            s.am.unregisterLoader(EffekseerLoader.class);
            s.am = null;
        }

        s.core.delete();
        EffekseerBackendCore.Terminate();
        state.remove();
    }

    /**
     * Update the effekseer instance
     * @param tpf time in seconds 
     */
    public static void update(float tpf) {
        State state = getState();
        while (state.garbagePile.size() != 0) {
            List<Integer> garbage = state.garbagePile.poll();
            for (int i : garbage) {
                stopEffect(i);
            }
        }
        float t = tpf / (1.0f / 60.0f);
        state.core.Update(t);        
    }

    public static void beginRender() {
        beginRender((Collection<Spatial>) null);
    }

    /**
     * Select a scene for rendering and update.
     * @param parent Only effects that are child of the parent spatial (or attached to the parent spatial itself) will be updated and rendered
     */
    public static void beginRender(Spatial parent) {
        State state = getState();
        state.v1SpatialList.set(0, parent);
        beginRender(state.v1SpatialList);
    }

    private static int getLayer(Spatial parent) {
        Integer layerId = parent.getUserData("_effekseer_layer");
        if (layerId == null) {
            State state = getState();
            for(int i = 0; i < 30; i++) {
                if (state.bitLayers[i] == null || state.bitLayers[i].get() == null) {
                    state.bitLayers[i] = new WeakReference<>(parent);
                    layerId = i + 2;
                    break;
                }
            }
            parent.setUserData("_effekseer_layer",layerId);
        }
        assert layerId != null : "Too many layers?";
        return layerId;
    }

    /**
     * Select a scene for rendering and update. Same as beginScene(Spatial) but accepts multiple parents.
     * @param parents Only effects that are child of the parent spatials (or attached to the parent spatials) will be updated and rendered
     */
    public static void beginRender(Collection<Spatial> parents) {
        State state = getState();
        state.currentSceneParents = parents;
        if (parents == null) {
            return;
        }

        for (Entry<EffekseerEmitterControl,EmitterState> e : state.emitters.entrySet()) {
            EffekseerEmitterControl emitter = e.getKey();           
            Spatial parent= parents.stream().filter(emitter::isChildOf).findAny().orElse(null);
            if (parent != null) {
                int l = getLayer(parent);
                emitter.setLayer(l);
            }
        }
    }

    /**
     * This must be called after the rendering, to deselect the scene and reset temporary states.
     */
    public static void endRender() {
        State state = getState();
        state.currentSceneParents = null;
    }

     /**
     * Render the scene
     * @param renderer GLRenderer
     * @param cam Camera
     * @param renderTarget framebuffer to which the particles will be rendered
     * @param sceneDepth depth of the scene, used for culling and soft particles
     */
    public static void render(
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
        assert sceneDepth == null || sceneDepth.getImage().getMultiSamples() <= 1 : "Multisampled depth is not supported!";

        State state = getState();
             
        GLRenderer gl = (GLRenderer)renderer;
         
        gl.setFrameBuffer(renderTarget);


        if (isOrthographic) {
            state.core.SetViewProjectionMatrixWithSimpleWindow(cam.getWidth(),cam.getHeight());
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

        int layer=0;
        for (Spatial p:state.currentSceneParents) {
            layer |= 1 << getLayer(p);
        }
        state.core.DrawBack(layer);
        state.core.DrawFront(layer);
    }

    public static int playEffect(EffekseerEffectCore e) {
        State state = getState();
        return state.core.Play(e);
    }

    public static void pauseEffect(int e,boolean v) {
        State state = getState();
        if (state.core.Exists(e)) {
            state.core.SetPaused(e, v);
        }
    }

    public static void stopEffect(int e) {
        State state = getState();
        if (state.core.Exists(e)) {
            state.core.Stop(e);
        }
    }

    public static  void setEffectVisibility(int e, boolean v) {
        State state = getState();
        if (state.core.Exists(e)) {
            state.core.SetShown(e,v);
        }
    }

    public static boolean isEffectAlive(int e) {
        State state = getState();
        return state.core.Exists(e);
    }

    public static void setEffectLayer(int handle, int layer) {
        State state = getState();
        if (state.core.Exists(handle)) {
            state.core.SetLayer(handle,layer);
        }
    }

    public static void setDynamicInput(int e, int index, float value) {
        State state = getState();
        if (state.core.Exists(e)) {
            state.core.SetDynamicInput(e,index,value);
        }
    }

    public static void setEffectTransform(int handler, Transform tr) {
        State state = getState();
        if (!state.core.Exists(handler)) {
            return;
        }
        state.m4.setTranslation(tr.getTranslation());
        state.m4.setRotationQuaternion(tr.getRotation());
        state.m4.setScale(tr.getScale());

        state.m4.get(state.v16, true);
        state.core.SetEffectTransformBaseMatrix(handler, state.v16[0], state.v16[1], state.v16[2], state.v16[3], state.v16[4], state.v16[5], state.v16[6], state.v16[7], state.v16[8], state.v16[9], state.v16[10], state.v16[11]);
    }

    public static LoadedEffect loadEffect(AssetManager am, String path) throws IOException {
        InputStream is = EffekseerUtils.openStream(am, "", path);
        return loadEffect(am,path,is);
    }

    public static LoadedEffect loadEffect(AssetManager am, String path, InputStream is) throws IOException {
        byte[] data = EffekseerUtils.readAll(is);
        return loadEffect(am, path, data);
    }

    public static LoadedEffect loadEffect(AssetManager am, String path, byte[] data) throws IOException {
        byte[] bytes = data;

        String root = path.contains("/") ? path.substring(0, path.lastIndexOf("/")) : "";
        assert !path.endsWith("/");

        EffekseerEffectCore effectCore = new EffekseerEffectCore();
        if (!effectCore.Load(bytes, bytes.length, 1f)) {
            throw new AssetLoadException("Can't load effect "+path);
        }

        EffekseerTextureType[] textureTypes = new EffekseerTextureType[] {
            EffekseerTextureType.Color,
            EffekseerTextureType.Normal,
            EffekseerTextureType.Distortion
        };

        // Textures
        for (int t = 0; t < 3; t++) {
            for(int i = 0; i < effectCore.GetTextureCount(textureTypes[t]); i++) {
                String p = effectCore.GetTexturePath(i, textureTypes[t]);
                InputStream iss = EffekseerUtils.openStream(am,root,p);
                bytes = EffekseerUtils.readAll(iss);
                if (!effectCore.LoadTexture(bytes, bytes.length, i, textureTypes[t])) {
                    throw new AssetLoadException("Can't load effect texture " + p);
                }  else {
                    // System.out.println("Load textures "+bytes.length+" bytes");
                }
            }
        }

        // Models
        for (int i = 0; i < effectCore.GetModelCount(); i++) {
            String p = effectCore.GetModelPath(i);
            InputStream iss = EffekseerUtils.openStream(am, root, p);
            bytes = EffekseerUtils.readAll(iss);
            if (!effectCore.LoadModel(bytes, bytes.length, i)) {
                throw new AssetLoadException("Can't effect load model " + p);
            }        
        }

        // Materials
        for (int i = 0; i < effectCore.GetMaterialCount(); i++) {
            String p = effectCore.GetMaterialPath(i);
            InputStream iss = EffekseerUtils.openStream(am, root, p);
            bytes = EffekseerUtils.readAll(iss);
            if (!effectCore.LoadMaterial(bytes, bytes.length, i) ) {
                throw new AssetLoadException("Can't load effect material "+p);
            }        
        }

        // Sounds?

        // State state = getState();

        LoadedEffect e = new LoadedEffect();
        e.core = effectCore;
        e.path = path;

        return e;
    }

    public static void registerEmitter(EffekseerEmitterControl e) {
        State state = getState();
        state.emitters.put(e,new EmitterState());
        e.setGarbagePile(state.garbagePile);
    }

    // public static void unregisterEmitter(EffekseerEmitterControl e) {
    //     State state = getState();
    //     state.emitters.remove(e);
    // }
}
