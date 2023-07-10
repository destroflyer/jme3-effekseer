package com.destroflyer.jme3.effekseer.nativ;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;

import java.util.ArrayList;

public class EffekseerProcessor implements SceneProcessor {

    public EffekseerProcessor(AssetManager assetManager, boolean sRGB, boolean isOrthographic, boolean hasDepth) {
        this.assetManager = assetManager;
        this.sRGB = sRGB;
        this.isOrthographic = isOrthographic;
        this.hasDepth = hasDepth;
        controls = new ArrayList<>();
    }
    private AssetManager assetManager;
    private boolean sRGB;
    private boolean isOrthographic;
    private boolean hasDepth;
    private RenderManager renderManager;
    private ViewPort viewPort;
    private EffekseerFrameBufferUtils frameBufferUtils;
    private boolean initialized;
    private ArrayList<EffekseerControl> controls;
    private FrameBuffer renderTarget;

    @Override
    public void initialize(RenderManager renderManager, ViewPort viewPort) {
        this.renderManager = renderManager;
        this.viewPort = viewPort;
        frameBufferUtils = new EffekseerFrameBufferUtils(renderManager);
        initialized = true;
    }

    @Override
    public void reshape(ViewPort viewPort, int width, int height) {

    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void preFrame(float tpf) {
        controls.clear();
        for (Spatial scene : viewPort.getScenes()) {
            scene.depthFirstTraversal(spatial -> {
                EffekseerControl control = spatial.getControl(EffekseerControl.class);
                if (control != null) {
                    controls.add(control);
                }
            });
        }
    }

    @Override
    public void postQueue(RenderQueue rq) {

    }

    @Override
    public void postFrame(FrameBuffer out) {
        updateRenderTarget(out, sRGB);
        for (EffekseerControl control : controls) {
            render(control, renderTarget);
        }
        frameBufferUtils.blitFrameBuffer(renderManager, renderTarget, out, true, hasDepth, viewPort.getCamera().getWidth(), viewPort.getCamera().getHeight());
    }

    private void updateRenderTarget(FrameBuffer in, boolean sRGB) {
        int width = viewPort.getCamera().getWidth();
        int height = viewPort.getCamera().getHeight();

        Format depthFormat = Format.Depth;
        Format colorFormat = Format.RGB16F;

        if (renderTarget == null || renderTarget.getWidth() != width || renderTarget.getHeight() != height || renderTarget.isSrgb() != sRGB) {
            // System.out.println("Create render target " + width  + "x" + height);
            if (renderTarget != null) {
                renderTarget.dispose();
            }
            renderTarget = new FrameBuffer(width, height,1);
            renderTarget.setDepthTexture(new Texture2D(width, height, 1, depthFormat));
            renderTarget.setColorTexture(new Texture2D(width, height, 1, colorFormat));
            if (sRGB) {
                // System.out.println("Enable sRGB");
                renderTarget.getColorBuffer().getTexture().getImage().setColorSpace(ColorSpace.sRGB);
                renderTarget.setSrgb(sRGB);
            }
        }

        FrameBuffer ofb = frameBufferUtils.bindFrameBuffer(renderManager,renderTarget);
        frameBufferUtils.clearFrameBuffer(renderManager, renderTarget, false, !hasDepth, true, ColorRGBA.BlackNoAlpha);
        frameBufferUtils.blitFrameBuffer(renderManager, in, renderTarget, true, hasDepth, renderTarget.getWidth(), renderTarget.getHeight());
        frameBufferUtils.bindFrameBuffer(renderManager, ofb);
    }

    private void render(EffekseerControl control, FrameBuffer sceneBuffer) {
        Camera camera = viewPort.getCamera();
        int width = camera.getWidth();
        int height = camera.getHeight();

        EffekseerFrameBufferUtils.FrameBufferCopy copy = frameBufferUtils.copyFrameBuffer(assetManager, renderManager, sceneBuffer, width, height, true, 0, true);
        Texture depth = copy.depthTx;
        Texture scene = copy.colorTx;

        FrameBuffer oldFrameBuffer = frameBufferUtils.bindFrameBuffer(renderManager, sceneBuffer);

        control.getManager().beginRender(viewPort.getScenes());
        control.getManager().render(renderManager.getRenderer(), camera, sceneBuffer, scene, depth, isOrthographic);
        control.getManager().endRender();

        frameBufferUtils.bindFrameBuffer(renderManager, oldFrameBuffer);
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void setProfiler(AppProfiler profiler) {

    }
}
