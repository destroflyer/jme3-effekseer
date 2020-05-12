package com.destroflyer.jme3.effekseer.renderer;

import com.destroflyer.jme3.effekseer.model.*;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.BillboardControl;
import com.jme3.texture.Texture;

import java.util.ArrayList;

public class GeometryFactory {

    static Geometry createGeometry_Sprite(ParticleNode particleNode, EffectiveUvValues effectiveUvValues, AssetManager assetManager) {
        SpriteDrawingValues spriteDrawingValues = (SpriteDrawingValues) particleNode.getDrawingValues();
        Texture texture = loadTexture(particleNode, assetManager);
        SpriteVertexPositions spriteVertexPositions = spriteDrawingValues.getVertexPositions();
        SpriteMesh spriteMesh = new SpriteMesh(effectiveUvValues, spriteVertexPositions);
        spriteMesh.initialize(texture);
        Geometry geometry = new Geometry(particleNode.getName() + "_geometry", spriteMesh);
        Material material = createUnshadedMaterialWithTexture(texture, assetManager);
        geometry.setMaterial(material);
        setCommonGeometryProperties(geometry, particleNode);
        setBillboard(geometry, spriteDrawingValues.getBillboard());
        return geometry;
    }

    static Geometry createGeometry_Track(ParticleNode particleNode, EffectiveUvValues effectiveUvValues, EffectiveTrackDrawingValues effectiveTrackDrawingValues, ArrayList<Particle> trackParticles, AssetManager assetManager) {
        TrackDrawingValues trackDrawingValues = (TrackDrawingValues) particleNode.getDrawingValues();
        TrackMesh trackMesh = new TrackMesh(effectiveUvValues, effectiveTrackDrawingValues, trackDrawingValues, trackParticles);
        return createGeometry_Custom(particleNode, trackMesh, assetManager);
    }

    static Geometry createGeometry_Ribbon(ParticleNode particleNode, EffectiveUvValues effectiveUvValues, ArrayList<Particle> ribbonParticles, AssetManager assetManager) {
        RibbonDrawingValues ribbonDrawingValues = (RibbonDrawingValues) particleNode.getDrawingValues();
        RibbonMesh ribbonMesh = new RibbonMesh(effectiveUvValues, ribbonDrawingValues, ribbonParticles);
        return createGeometry_Custom(particleNode, ribbonMesh, assetManager);
    }

    static Geometry createGeometry_Ring(ParticleNode particleNode, EffectiveUvValues effectiveUvValues, EffectiveRingDrawingValues effectiveRingDrawingValues, AssetManager assetManager) {
        RingDrawingValues ringDrawingValues = (RingDrawingValues) particleNode.getDrawingValues();
        RingMesh ringMesh = new RingMesh(effectiveUvValues, effectiveRingDrawingValues, ringDrawingValues);
        Geometry geometry = createGeometry_Custom(particleNode, ringMesh, assetManager);
        setBillboard(geometry, ringDrawingValues.getBillboard());
        return geometry;
    }

    static Geometry createGeometry_Custom(ParticleNode particleNode, ParticleMesh particleMesh, AssetManager assetManager) {
        Geometry geometry = new Geometry(particleNode.getName(), particleMesh);
        Texture texture = loadTexture(particleNode, assetManager);
        particleMesh.initialize(texture);
        Material material = createUnshadedMaterialWithTexture(texture, assetManager);
        material.setBoolean("VertexColor", true);
        geometry.setMaterial(material);
        setCommonGeometryProperties(geometry, particleNode);
        return geometry;
    }

    static void setBillboard(Geometry geometry, int billboard) {
        // Billboard or RotatedBillboard
        if ((billboard == 0) || (billboard == 3)) {
            geometry.addControl(new BillboardControl());
        }
    }

    private static void setCommonGeometryProperties(Geometry geometry, ParticleNode particleNode) {
        Material material = geometry.getMaterial();
        setBlendingProperties(material.getAdditionalRenderState(), particleNode.getRendererCommonValues().getAlphaBlend());
        material.getAdditionalRenderState().setDepthWrite(particleNode.getRendererCommonValues().isZWrite());
        material.getAdditionalRenderState().setDepthTest(particleNode.getRendererCommonValues().isZTest());
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
    }

    private static void setBlendingProperties(RenderState renderState, int alphaBlend) {
        switch (alphaBlend) {
            case 0:
                renderState.setBlendMode(RenderState.BlendMode.Off);
                renderState.setBlendEquation(RenderState.BlendEquation.Add);
            case 1:
            case 2:
                renderState.setBlendMode(RenderState.BlendMode.AlphaAdditive);
                renderState.setBlendEquation(RenderState.BlendEquation.Add);
                break;
            case 3:
                renderState.setBlendMode(RenderState.BlendMode.AlphaAdditive);
                renderState.setBlendEquation(RenderState.BlendEquation.Subtract);
                break;
        }
    }

    private static Material createUnshadedMaterialWithTexture(Texture texture, AssetManager assetManager) {
        // TODO: Use custom material including according shader for distortion
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        if (texture != null) {
            material.setTexture("ColorMap", texture);
        }
        return material;
    }

    private static Texture loadTexture(ParticleNode particleNode, AssetManager assetManager) {
        RendererCommonValues rendererCommonValues = particleNode.getRendererCommonValues();
        String texturePath = rendererCommonValues.getColorTexture();
        // TODO: Research what referencing effekseer files as texture actually does
        if ((texturePath != null) && (!texturePath.endsWith(".efkproj"))) {
            try {
                Texture texture = assetManager.loadTexture(new TextureKey(texturePath, false));
                // TODO: Support Clamp
                texture.setWrap(Texture.WrapMode.Repeat);
                return texture;
            } catch (AssetNotFoundException ex) {
                // TODO: Log warning
            }
        }
        return null;
    }

    static void updateParticleGeometry(Particle particle) {
        EffectiveDrawingValues effectiveDrawingValues = particle.getEffectiveDrawingValues();
        if (effectiveDrawingValues instanceof EffectiveSpriteDrawingValues) {
            // Materials
            EffectiveSpriteDrawingValues effectiveSpriteDrawingValues = (EffectiveSpriteDrawingValues) effectiveDrawingValues;
            EffectiveColorValues effectiveColorValues = effectiveSpriteDrawingValues.getEffectiveColorValues();
            ColorRGBA currentColor = effectiveColorValues.getCurrentValue();
            ColorRGBA effectiveFadedColor = getEffectiveFadedColor(particle, currentColor);
            Material material = particle.getGeometry().getMaterial();
            material.setColor("Color", effectiveFadedColor);
        } else if (effectiveDrawingValues instanceof EffectiveRingDrawingValues) {
            RingMesh ringMesh = (RingMesh) particle.getGeometry().getMesh();
            ringMesh.updateBuffers();
        }
        updateGeometry(particle, particle.getGeometry());
    }

    static void updateGeometry(Particle progressedParticle, Geometry geometry) {
        float passedLife = (progressedParticle.getStartingLife() - progressedParticle.getRemainingLife());
        ParticleMesh particleMesh = (ParticleMesh) geometry.getMesh();
        particleMesh.updateTextureCoordinates(passedLife);
    }

    static ColorRGBA getEffectiveFadedColor(Particle particle, ColorRGBA color) {
        RendererCommonValues rendererCommonValues = particle.getEmitter().getParticleNode().getRendererCommonValues();
        float alphaFactor = 1;
        FadeValues fadeIn = rendererCommonValues.getFadeIn();
        if (fadeIn != null) {
            float passedLife = (particle.getStartingLife() - particle.getRemainingLife());
            if (passedLife < fadeIn.getDuration()) {
                // TODO: Easing instead of linear
                alphaFactor = (passedLife / fadeIn.getDuration());
            }
        }
        FadeValues fadeOut = rendererCommonValues.getFadeOut();
        if (fadeOut != null) {
            float remainingLife = particle.getRemainingLife();
            if (remainingLife < fadeOut.getDuration()) {
                // TODO: Easing instead of linear
                alphaFactor = (remainingLife / fadeOut.getDuration());
            }
        }
        ColorRGBA fadedColor = new ColorRGBA();
        fadedColor.set(color.getRed(), color.getGreen(), color.getBlue(), alphaFactor * color.getAlpha());
        return fadedColor;
    }
}
