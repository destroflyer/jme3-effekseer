package com.destroflyer.jme3.effekseer.nativ;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Caps;
import com.jme3.renderer.RenderContext;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.opengl.GL;
import com.jme3.renderer.opengl.GLFbo;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Map;
import java.util.WeakHashMap;

public class EffekseerFrameBufferUtils {

    public static class FrameBufferCopy {
        FrameBuffer fb;
        Geometry copier;
        Texture2D depthTx;
        Texture2D colorTx;
        Format colorFormat;
        Format depthFormat;
    }

    private static class State {
        private RenderContext renderContext;
        private GLFbo glfbo;
        private Map<FrameBuffer,FrameBufferCopy> copyBuffer = new WeakHashMap<>();
        private ColorRGBA tmpColor = new ColorRGBA();
    }

    public EffekseerFrameBufferUtils(RenderManager renderManager) {
        initState(renderManager);
    }
    private State state;

    private void initState(RenderManager renderManager) {
        state = new State();
        if (renderManager.getRenderer() == null || !(renderManager.getRenderer() instanceof GLRenderer)) {
            throw new RendererException("Only GLRenderer renderer supported");
        }
        GLRenderer renderer = (GLRenderer) renderManager.getRenderer();
        try {
            if (state.renderContext == null || state.glfbo == null) {
                for (Field field : renderer.getClass().getDeclaredFields()) {
                    Class type = field.getType();
                    if (RenderContext.class.isAssignableFrom(type)) {
                        field.setAccessible(true);
                        state.renderContext = (RenderContext) field.get(renderer);
                    } else if (GLFbo.class.isAssignableFrom(type)) {
                        field.setAccessible(true);
                        state.glfbo = (GLFbo)field.get(renderer);
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void blitFrameBuffer(RenderManager renderManager, FrameBuffer src, FrameBuffer dst, boolean copyColor, boolean copyDepth, int width, int height) {
        EnumSet<Caps> caps = renderManager.getRenderer().getCaps();
        GLRenderer renderer = (GLRenderer) renderManager.getRenderer();

        // Camera cam = renderManager.getCurrentCamera();
        // int vpX = (int) (cam.getViewPortLeft() * cam.getWidth());
        // int vpY = (int) (cam.getViewPortBottom() * cam.getHeight());
        // int viewX2 = (int) (cam.getViewPortRight() * cam.getWidth());
        // int viewY2 = (int) (cam.getViewPortTop() * cam.getHeight());
        // int vpW = viewX2 - vpX;
        // int vpH = viewY2 - vpY;

        int vpX = 0;
        int vpY = 0;
        int vpW = width;
        int vpH = height;

        if (caps.contains(Caps.FrameBufferBlit)) {
            int srcX0 = 0;
            int srcY0 = 0;
            int srcX1;
            int srcY1;

            int dstX0 = 0;
            int dstY0 = 0;
            int dstX1;
            int dstY1;

            int prevFBO = state.renderContext.boundFBO;

            if ((src != null) && src.isUpdateNeeded()) {
                renderer.updateFrameBuffer(src);
            }

            if ((dst != null) && dst.isUpdateNeeded()) {
                renderer.updateFrameBuffer(dst);
            }

            if (src == null) {
                state.glfbo.glBindFramebufferEXT(GLFbo.GL_READ_FRAMEBUFFER_EXT, 0);
                srcX0 = vpX;
                srcY0 = vpY;
                srcX1 = vpX + vpW;
                srcY1 = vpY + vpH;
            } else {
                state.glfbo.glBindFramebufferEXT(GLFbo.GL_READ_FRAMEBUFFER_EXT, src.getId());
                srcX1 = width;
                srcY1 = height;
            }
            if (dst == null) {
                state.glfbo.glBindFramebufferEXT(GLFbo.GL_DRAW_FRAMEBUFFER_EXT, 0);
                dstX0 = vpX;
                dstY0 = vpY;
                dstX1 = vpX + vpW;
                dstY1 = vpY + vpH;
            } else {
                state. glfbo.glBindFramebufferEXT(GLFbo.GL_DRAW_FRAMEBUFFER_EXT, dst.getId());
                dstX1 = width;
                dstY1 = height;
            }
            int mask = 0;
            if (copyColor) {
                mask |= GL.GL_COLOR_BUFFER_BIT;
            }
            if (copyDepth) {
                mask |= GL.GL_DEPTH_BUFFER_BIT;
            }
            state.glfbo.glBlitFramebufferEXT(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, GL.GL_NEAREST);
            state.glfbo.glBindFramebufferEXT(GLFbo.GL_FRAMEBUFFER_EXT, prevFBO);
        } else {
            throw new RendererException("Framebuffer blitting not supported by the video hardware");
        }
    }

    public void clearFrameBuffer(RenderManager renderManager, FrameBuffer frameBuffer, boolean color, boolean depth, boolean stencil, ColorRGBA bgColor) {
        FrameBuffer ofb = bindFrameBuffer(renderManager, frameBuffer);
        state.tmpColor.set(state.renderContext.clearColor);
        renderManager.getRenderer().setBackgroundColor(bgColor);
        renderManager.getRenderer().clearBuffers(color, depth, stencil);
        renderManager.getRenderer().setBackgroundColor(state.tmpColor);
        bindFrameBuffer(renderManager, ofb);
    }

    public FrameBuffer bindFrameBuffer(RenderManager renderManager, FrameBuffer frameBuffer) {
        FrameBuffer cfb = state.renderContext.boundFB;
        if (cfb != frameBuffer) {
            renderManager.getRenderer().setFrameBuffer(frameBuffer);
        }
        return cfb;
    }

    public FrameBufferCopy copyFrameBuffer(AssetManager assetManager, RenderManager renderManager, FrameBuffer in, int width, int height, boolean copyColor, int colorTarget, boolean copyDepth) {
        Format depthFormat = Format.R32F;
        Format colorFormat = Format.RGB16F;

        FrameBufferCopy fbc = state.copyBuffer.get(in);
        FrameBuffer target = fbc != null ? fbc.fb : null;

        if (target == null || target.getWidth() != width || target.getHeight() != height || fbc.depthFormat != depthFormat || fbc.colorFormat != colorFormat || target.getColorBuffer(0).getFormat() != colorFormat) {
            if (target != null) {
                target.dispose();
            }
            target = new FrameBuffer(width,height,1);
            fbc = new FrameBufferCopy();
            fbc.fb = target;
            fbc.colorFormat = colorFormat;
            fbc.depthFormat = depthFormat;
            if (copyColor) {
                target.addColorTexture(fbc.colorTx = new Texture2D(width, height, colorFormat));
            }
            if (copyDepth) {
                target.addColorTexture(fbc.depthTx = new Texture2D(width, height, depthFormat));
            }
            if (target.getNumColorBuffers() > 1) {
                target.setMultiTarget(true);
            }
            state.copyBuffer.put(in, fbc);
        }

        // Copy
        int samples = in.getSamples();
        Texture inColor = copyColor ? in.getColorBuffer(colorTarget).getTexture() : null;
        Texture inDepth = copyDepth ? in.getDepthBuffer().getTexture() : null;

        Geometry geometry = fbc.copier;

        if (geometry == null) {
            geometry = fbc.copier = new Geometry("Copier", new Quad(1, 1));
            geometry.setMaterial(new Material(assetManager, "Effekseer/Copier/Copier.j3md"));
        }

        geometry.getMaterial().setTexture("Color", inColor);
        geometry.getMaterial().setTexture("Depth", inDepth);
        geometry.getMaterial().setInt("NumSamples", samples);

        FrameBuffer ofb = bindFrameBuffer(renderManager, target);
        renderManager.renderGeometry(geometry);
        bindFrameBuffer(renderManager, ofb);

        return fbc;
    }
}
