package com.destroflyer.jme3.effekseer.nativ.jme3;

import com.destroflyer.jme3.effekseer.nativ.Effekseer;
import com.destroflyer.jme3.effekseer.nativ.EffekseerUpdater;
import com.destroflyer.jme3.effekseer.nativ.EffekseerUtils;
import com.destroflyer.jme3.effekseer.nativ.EffekseerUtils.FrameBufferCopy;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;

public class EffekseerRenderer {

    public static EffekseerRenderer addToViewPort(AppStateManager stateManager, ViewPort vp, AssetManager am, boolean sRGB) {
        return addToViewPort(stateManager, vp, am, sRGB, false);
    }

    public static EffekseerRenderer addToViewPort(AppStateManager stateManager, ViewPort vp, AssetManager am, boolean sRGB, boolean isOrthographic) {
        return addToViewPort(stateManager, vp, am, sRGB, isOrthographic, true);
    }

    public static EffekseerRenderer addToViewPort(AppStateManager stateManager, ViewPort vp, AssetManager am, boolean sRGB, boolean isOrthographic, boolean hasDepth) {
        if (stateManager.getState(EffekseerUpdater.class) == null) {
            stateManager.attach(new EffekseerUpdater());
        }

        FilterPostProcessor fpp = null;
        if (!vp.getName().equals("Gui Default")) { // Detect when attached to default guiViewPort in jme.
            for (SceneProcessor p:vp.getProcessors()) {
                if (p instanceof FilterPostProcessor) {
                    fpp = (FilterPostProcessor) p;
                    break;
                }
            }
        } else {
            // System.out.println("Detected default Gui View Port");
            isOrthographic = true;
            hasDepth = false;
        }

        if (fpp != null) {
            EffekseerFilter filter = new EffekseerFilter(am, sRGB);
            fpp.addFilter(filter);
            return filter.getRenderer();
        } else {
            EffekseerProcessor p = new EffekseerProcessor(am, sRGB, isOrthographic, hasDepth);
            vp.addProcessor(p);
            return p.getRenderer();
        }
    }

    public EffekseerRenderer(AssetManager manager, boolean sRGB) {
        Effekseer.init(manager,sRGB);
        this.assetManager = manager;
    }
    protected FrameBuffer renderTarget;
    protected float tpf;
    private ViewPort viewPort;
    private RenderManager renderManager;
    private FrameBuffer sceneBuffer;
    private AssetManager assetManager;
    private boolean isOrthographic;

    public void setAsync( int nThreads) {
        Effekseer.setAsync(nThreads);
    }

    protected void setViewPort(ViewPort vp) {
        viewPort = vp;
    }

    public void setRenderManager(RenderManager rm) {
        renderManager = rm;
    }

    public Texture getTexture() {
        if (renderTarget == null) {
            return null;
        }
        return renderTarget.getColorBuffer().getTexture();
    }
    
    public void setTpf(float tpf) {
        this.tpf = tpf;
    }

    public void setSceneBuffer(FrameBuffer sceneBuffer) {
        this.sceneBuffer = sceneBuffer;
    }

    public void setOrthographic(boolean v) {
        isOrthographic = v;
    }

    protected void render() {
        Camera cam = viewPort.getCamera();

        int width = cam.getWidth();
        int height = cam.getHeight();
    
        FrameBufferCopy copy = EffekseerUtils.copyFrameBuffer(assetManager, renderManager, sceneBuffer, width, height, true, 0, true);
        Texture depth = copy.depthTx;
        Texture scene = copy.colorTx;

        FrameBuffer oldFb = EffekseerUtils.bindFrameBuffer(renderManager, sceneBuffer);

        Effekseer.beginRender(viewPort.getScenes());
        Effekseer.render(renderManager.getRenderer(), cam, sceneBuffer, scene, depth, isOrthographic);
        Effekseer.endRender();

        EffekseerUtils.bindFrameBuffer(renderManager, oldFb);
    }
}
