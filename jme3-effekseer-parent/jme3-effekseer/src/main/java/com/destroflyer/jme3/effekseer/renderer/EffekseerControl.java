package com.destroflyer.jme3.effekseer.renderer;

import com.destroflyer.jme3.effekseer.model.*;
import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import java.util.*;
import java.util.function.Function;

import static com.destroflyer.jme3.effekseer.renderer.DrawingHelper.*;
import static com.destroflyer.jme3.effekseer.renderer.RangeValues.*;
import static com.destroflyer.jme3.effekseer.renderer.DynamicValuesHelper.*;
import static com.destroflyer.jme3.effekseer.renderer.TransformHelper.*;
import static com.destroflyer.jme3.effekseer.renderer.UvHelper.*;

public class EffekseerControl extends AbstractControl {

    public EffekseerControl(ParticleEffect particleEffect, ParticleEffectSettings settings, AssetManager assetManager) {
        this.particleEffect = particleEffect;
        this.settings = settings;
        this.assetManager = assetManager;
    }
    private ParticleEffect particleEffect;
    private ParticleEffectSettings settings;
    private AssetManager assetManager;
    private float currentFrame = -1;
    private LinkedList<ParticleEmitter> particleEmitters = new LinkedList<>();
    private HashMap<ParticleEmitter, ParticleEmitterRendering> particleEmitterRenderings = new HashMap<>();
    private LinkedList<ParticleEmitter> oldParticleEmitters = new LinkedList<>();
    private HashMap<ParticleEmitter, ParticleEmitterRendering> oldParticleEmitterRenderings = new HashMap<>();
    private Vector3f cameraDirection = new Vector3f(0, 0, 1);

    @Override
    protected void controlUpdate(float tpf) {
        if (currentFrame == -1) {
            start();
        }
        if (currentFrame < particleEffect.getEndFrame()) {
            float frameProgress = (tpf / settings.getFrameLength());
            currentFrame += frameProgress;
            oldParticleEmitters.clear();
            oldParticleEmitters.addAll(particleEmitters);
            for (ParticleEmitter particleEmitter : oldParticleEmitters) {
                countAndEmitIfAllowed(particleEmitter, frameProgress);
            }
            oldParticleEmitters.clear();
            oldParticleEmitters.addAll(particleEmitters);
            for (ParticleEmitter particleEmitter : oldParticleEmitters) {
                checkEmitterDelay(particleEmitter, frameProgress);
            }
            oldParticleEmitterRenderings.clear();
            oldParticleEmitterRenderings.putAll(particleEmitterRenderings);
            for (ParticleEmitterRendering particleEmitterRendering : oldParticleEmitterRenderings.values()) {
                updateParticleEmitterRendering(particleEmitterRendering, frameProgress);
                LinkedList<Particle> particleToUpdate = new LinkedList<>(particleEmitterRendering.getParticles());
                for (Particle particle : particleToUpdate) {
                    checkAndUpdateParticle(particle, frameProgress);
                }
            }
        } else {
            cleanup();
            if (settings.isLoop()) {
                start();
            } else {
                spatial.removeControl(this);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
        cameraDirection.set(viewPort.getCamera().getDirection());
    }

    private void start() {
        particleEmitters.clear();
        particleEmitterRenderings.clear();
        for (ParticleNode particleNode : particleEffect.getRoot().getChildren()) {
            addEmitter(null, particleNode);
        }
        // TODO: Calculate frames until start frame
        currentFrame = particleEffect.getStartFrame();
    }

    private void addEmitter(Particle parentParticle, ParticleNode particleNode) {
        ParticleEmitter particleEmitter = new ParticleEmitter();
        particleEmitter.setParentParticle(parentParticle);
        particleEmitter.setParticleNode(particleNode);
        particleEmitter.setRemainingEmitDelay(generateRangeValue1f(particleNode.getCommonValues().getGenerationTimeOffset()));
        float spawnInterval = generateRangeValue1f(particleNode.getCommonValues().getGenerationTime());
        particleEmitter.setSpawnInterval(spawnInterval);
        particleEmitter.setFramesSinceLastEmit(spawnInterval);
        particleEmitter.setGeneration(0);
        particleEmitters.add(particleEmitter);
        countAndEmitIfAllowed(particleEmitter, 0);
    }

    private void countAndEmitIfAllowed(ParticleEmitter particleEmitter, float frameProgress) {
        if (particleEmitter.getRemainingEmitDelay() <= 0) {
            particleEmitter.setFramesSinceLastEmit(particleEmitter.getFramesSinceLastEmit() + frameProgress);
            while (isEmitAllowed(particleEmitter)) {
                emit(particleEmitter);
                particleEmitter.setFramesSinceLastEmit(particleEmitter.getFramesSinceLastEmit() - particleEmitter.getSpawnInterval());
            }
        }
    }

    private boolean isEmitAllowed(ParticleEmitter particleEmitter) {
        if (particleEmitter.getFramesSinceLastEmit() >= particleEmitter.getSpawnInterval()) {
            MaxGeneration maxGeneration = particleEmitter.getParticleNode().getCommonValues().getMaxGeneration();
            if ((particleEmitter.getGeneration() < maxGeneration.getValue()) || maxGeneration.isInfinite()) {
                return true;
            }
        }
        return false;
    }

    private void emit(ParticleEmitter particleEmitter) {
        Particle particle = emitParticle(particleEmitter);
        for (ParticleNode childParticleNode : particleEmitter.getParticleNode().getChildren()) {
            addEmitter(particle, childParticleNode);
        }
    }

    private Particle emitParticle(ParticleEmitter particleEmitter) {
        ParticleNode particleNode = particleEmitter.getParticleNode();
        DrawingValues drawingValues = particleNode.getDrawingValues();

        Function<ParticleEmitter, ParticleEmitterRendering> particleEmitterRenderingFunction;
        if ((drawingValues instanceof TrackDrawingValues) || (drawingValues instanceof RibbonDrawingValues)) {
            particleEmitterRenderingFunction = pe -> {
                MergedParticleEmitterRendering mergedParticleEmitterRendering = new MergedParticleEmitterRendering();
                ArrayList<Particle> particles = new ArrayList<>();
                if (isRendered(particleNode)) {
                    Geometry mergedGeometry;
                    EffectiveUvValues effectiveUvValues = generateEffectiveUvValues(particleNode.getRendererCommonValues().getUvValues());
                    if (drawingValues instanceof TrackDrawingValues) {
                        EffectiveDrawingValues effectiveDrawingValues = generateEffectiveDrawingValues(drawingValues);
                        mergedGeometry = GeometryFactory.createGeometry_Track(particleNode, effectiveUvValues, (EffectiveTrackDrawingValues) effectiveDrawingValues, particles, assetManager);
                    } else {
                        mergedGeometry = GeometryFactory.createGeometry_Ribbon(particleNode, effectiveUvValues, particles, assetManager);
                    }
                    attachToRoot(mergedGeometry);
                    mergedParticleEmitterRendering.setMergedGeometry(mergedGeometry);
                }
                mergedParticleEmitterRendering.setParticles(particles);
                return mergedParticleEmitterRendering;
            };
        } else {
            particleEmitterRenderingFunction = pe -> {
                ParticleEmitterRendering particleEmitterRendering = new ParticleEmitterRendering();
                particleEmitterRendering.setParticles(new LinkedList<>());
                return particleEmitterRendering;
            };
        }

        ParticleEmitterRendering particleEmitterRendering = particleEmitterRenderings.computeIfAbsent(particleEmitter, particleEmitterRenderingFunction);
        Particle particle = new Particle();
        initializeParticle(particleEmitter, particle);
        // EffectiveDrawingValues
        EffectiveDrawingValues effectiveDrawingValues = null;
        if ((drawingValues instanceof SpriteDrawingValues) || (drawingValues instanceof RingDrawingValues) || (drawingValues instanceof RibbonDrawingValues)) {
            effectiveDrawingValues = generateEffectiveDrawingValues_Particle(drawingValues);
            particle.setEffectiveDrawingValues(effectiveDrawingValues);
        }
        // Geometry
        if ((drawingValues instanceof SpriteDrawingValues) || (drawingValues instanceof RingDrawingValues)) {
            if (isRendered(particleNode)) {
                Geometry geometry;
                if (drawingValues instanceof SpriteDrawingValues) {
                    geometry = GeometryFactory.createGeometry_Sprite(particleNode, particle.getEffectiveUvValues(), assetManager);
                } else {
                    geometry = GeometryFactory.createGeometry_Ring(particleNode, particle.getEffectiveUvValues(), (EffectiveRingDrawingValues) effectiveDrawingValues, assetManager);
                }
                attachToRoot(geometry);
                particle.setGeometry(geometry);
                GeometryFactory.updateParticleGeometry(particle);
            }
        }
        particleEmitterRendering.getParticles().add(particle);

        particleEmitter.setGeneration(particleEmitter.getGeneration() + 1);
        return particle;
    }

    private boolean isRendered(ParticleNode particleNode) {
        // TODO: Support distortion :)
        return (particleNode.isRendered() && (particleNode.getRendererCommonValues().getDistortionIntensity() == 0));
    }

    private void attachToRoot(Spatial spatial) {
        Node rootNode = (Node) this.spatial;
        rootNode.attachChild(spatial);
    }

    private void initializeParticle(ParticleEmitter particleEmitter, Particle particle) {
        particle.setEmitter(particleEmitter);

        Transform parentTransform = getParentTransform(particle);
        CommonValues commonValues = particleEmitter.getParticleNode().getCommonValues();
        Vector3f fixedParentLocation = getFixedParentTransform(parentTransform, commonValues.getLocationInheritance(), Transform::getTranslation);
        Quaternion fixedParentRotation = getFixedParentTransform(parentTransform, commonValues.getRotationInheritance(), Transform::getRotation);
        Vector3f fixedParentScale = getFixedParentTransform(parentTransform, commonValues.getScalingInheritance(), Transform::getScale);
        particle.setFixedParentLocation(cloneIfNotNull(fixedParentLocation));
        particle.setFixedParentRotation(cloneIfNotNull(fixedParentRotation));
        particle.setFixedParentScale(cloneIfNotNull(fixedParentScale));

        ParticleNode particleNode = particleEmitter.getParticleNode();
        particle.setEffectiveLocationValues(generateEffectiveDynamicValues(particleNode.getLocationValues()));
        particle.setEffectiveRotationValues(generateEffectiveDynamicValues(particleNode.getRotationValues()));
        particle.setEffectiveScalingValues(generateEffectiveDynamicValues(particleNode.getScalingValues()));

        particle.setTransform(new Transform());
        updateTransform(particle, 0);

        float life = generateRangeValue1f(particleNode.getCommonValues().getLife());
        particle.setStartingLife(life);
        particle.setRemainingLife(life);
        particle.setEffectiveUvValues(generateEffectiveUvValues(particleNode.getRendererCommonValues().getUvValues()));
    }

    private <T> T getFixedParentTransform(Transform parentTransform, TransformInheritance transformInheritance, Function<Transform, T> transformComponentFunction) {
        switch (transformInheritance) {
            case NEVER: return transformComponentFunction.apply(Transform.IDENTITY);
            case ONLY_ON_CREATE: return transformComponentFunction.apply(parentTransform);
            case ALWAYS: return null;
        }
        return null;
    }

    private void checkEmitterDelay(ParticleEmitter particleEmitter, float frameProgress) {
        float remainingEmitDelay = particleEmitter.getRemainingEmitDelay();
        if (remainingEmitDelay > 0) {
            remainingEmitDelay -= frameProgress;
            if (remainingEmitDelay < 0) {
                remainingEmitDelay = 0;
            }
            particleEmitter.setRemainingEmitDelay(remainingEmitDelay);
        }
    }

    private void updateParticleEmitterRendering(ParticleEmitterRendering particleEmitterRendering, float frameProgress) {
        if (particleEmitterRendering instanceof MergedParticleEmitterRendering) {
            MergedParticleEmitterRendering mergedParticleEmitterRendering = (MergedParticleEmitterRendering) particleEmitterRendering;
            if (mergedParticleEmitterRendering.getMergedGeometry() != null) {
                Geometry mergedGeometry = mergedParticleEmitterRendering.getMergedGeometry();
                MergedParticlesMesh mergedParticlesMesh = (MergedParticlesMesh) mergedGeometry.getMesh();
                // Logical update (Choose the last particle to determine the drawing progress)
                EffectiveDrawingValues effectiveDrawingValues = mergedParticlesMesh.getEffectiveDrawingValues();
                Particle lastParticle = mergedParticleEmitterRendering.getParticles().get(mergedParticleEmitterRendering.getParticles().size() - 1);
                updateCurrentDrawingValue(lastParticle, effectiveDrawingValues, frameProgress);
                // Visual update (Choose the first particle to determine the uv scrolling progress)
                mergedParticlesMesh.setCameraDirection(cameraDirection);
                mergedParticlesMesh.updateBuffers();
                Particle firstParticle = mergedParticleEmitterRendering.getParticles().get(0);
                GeometryFactory.updateGeometry(firstParticle, mergedGeometry);
            }
        }
    }

    private void checkAndUpdateParticle(Particle particle, float frameProgress) {
        if (checkParticleLife(particle, frameProgress)) {
            updateTransform(particle, frameProgress);
            if (particle.getEffectiveDrawingValues() != null) {
                if (particle.getGeometry() != null) {
                    GeometryFactory.updateParticleGeometry(particle);
                    particle.getGeometry().setLocalTransform(particle.getTransform());
                }
                updateCurrentDrawingValue(particle, particle.getEffectiveDrawingValues(), frameProgress);
            }
        } else {
            removeParticle(particle);
        }
    }

    private boolean checkParticleLife(Particle particle, float frameProgress) {
        boolean isAlive = true;
        if (particle.getRemainingLife() != -1) {
            float remainingLife = (particle.getRemainingLife() - frameProgress);
            particle.setRemainingLife(remainingLife);
            boolean destroyAfterTime = particle.getEmitter().getParticleNode().getCommonValues().isDestroyAfterTime();
            isAlive = ((!destroyAfterTime) || (remainingLife > 0));
        }
        return isAlive;
    }

    private void removeParticle(Particle particle) {
        Geometry geometry = particle.getGeometry();
        if (geometry != null) {
            particle.getGeometry().removeFromParent();
        }
        ParticleEmitterRendering particleEmitterRendering = particleEmitterRenderings.get(particle.getEmitter());
        particleEmitterRendering.getParticles().remove(particle);
        boolean wasLastParticleOfEmitter = particleEmitterRendering.getParticles().isEmpty();
        if (wasLastParticleOfEmitter) {
            // Remove merged geometry
            if (particleEmitterRendering instanceof MergedParticleEmitterRendering) {
                MergedParticleEmitterRendering mergedParticleEmitterRendering = (MergedParticleEmitterRendering) particleEmitterRendering;
                Geometry mergedGeometry = mergedParticleEmitterRendering.getMergedGeometry();
                mergedGeometry.getParent().detachChild(mergedGeometry);
            }
            // Remove children
            LinkedList<ParticleEmitter> existingParticleEmitters = new LinkedList<>(particleEmitters);
            for (ParticleEmitter otherParticleEmitter : existingParticleEmitters) {
                if (otherParticleEmitter.getParentParticle() == particle) {
                    particleEmitters.remove(otherParticleEmitter);
                }
            }
            particleEmitterRenderings.remove(particle.getEmitter());
        }
    }

    private void updateTransform(Particle particle, float frameProgress) {
        if (particle.getEffectiveLocationValues() != null) {
            updateCurrentLocalRelativeDynamicValue(particle, particle.getEffectiveLocationValues(), frameProgress);
            particle.getTransform().setTranslation(getWorldLocation(particle));
        }
        if (particle.getEffectiveRotationValues() != null) {
            updateCurrentLocalRelativeDynamicValue(particle, particle.getEffectiveRotationValues(), frameProgress);
            particle.getTransform().setRotation(getWorldRotation(particle));
        }
        if (particle.getEffectiveScalingValues() != null) {
            updateCurrentLocalRelativeDynamicValue(particle, particle.getEffectiveScalingValues(), frameProgress);
            particle.getTransform().setScale(getWorldScale(particle));
        }
    }

    private void cleanup() {
        for (ParticleEmitterRendering particleEmitterRendering : particleEmitterRenderings.values()) {
            if (particleEmitterRendering instanceof MergedParticleEmitterRendering) {
                MergedParticleEmitterRendering mergedParticleEmitterRendering = (MergedParticleEmitterRendering) particleEmitterRendering;
                Geometry mergedGeometry = mergedParticleEmitterRendering.getMergedGeometry();
                mergedGeometry.getParent().detachChild(mergedGeometry);
            }
            for (Particle particle : particleEmitterRendering.getParticles()) {
                Geometry geometry = particle.getGeometry();
                if (geometry != null) {
                    geometry.getParent().detachChild(geometry);
                }
            }
        }
    }
}
