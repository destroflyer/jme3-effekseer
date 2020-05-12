package com.destroflyer.jme3.effekseer.renderer;

import com.jme3.math.Vector2f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.TempVars;

import java.util.function.Function;

public abstract class ParticleMesh extends Mesh {

    public ParticleMesh(EffectiveUvValues effectiveUvValues) {
        this.effectiveUvValues = effectiveUvValues;
    }
    private EffectiveUvValues effectiveUvValues;
    private Texture texture;
    private boolean wasModified = true;

    public void initialize(Texture texture) {
        this.texture = texture;
        updateTextureCoordinates(0);
        wasModified = false;
    }

    protected void onModification() {
        wasModified = true;
    }

    void updateTextureCoordinates(float passedLife) {
        Function<float[], float[]> progressToTextureCoordinates = null;
        if (texture != null) {
            if (effectiveUvValues instanceof EffectiveScrollUvValues) {
                EffectiveScrollUvValues effectiveScrollUvValues = (EffectiveScrollUvValues) effectiveUvValues;
                TempVars tempVars = TempVars.get();
                Vector2f movedStart = tempVars.vect2d;
                Vector2f currentStart = tempVars.vect2d2;
                movedStart.set(effectiveScrollUvValues.getSpeed()).multLocal(passedLife);
                currentStart.set(effectiveScrollUvValues.getStart()).addLocal(movedStart);
                progressToTextureCoordinates = progressCoordinates -> getTextureCoordinates(progressCoordinates, currentStart, effectiveScrollUvValues.getSize());
                tempVars.release();
            } else if (wasModified) {
                if (effectiveUvValues instanceof EffectiveStandardUvValues) {
                    progressToTextureCoordinates = Function.identity();
                } else if (effectiveUvValues instanceof EffectiveFixedUvValues) {
                    EffectiveFixedUvValues effectiveFixedUvValues = (EffectiveFixedUvValues) effectiveUvValues;
                    progressToTextureCoordinates = progressCoordinates -> getTextureCoordinates(progressCoordinates, effectiveFixedUvValues.getStart(), effectiveFixedUvValues.getSize());
                }
            }
        }
        if (progressToTextureCoordinates != null) {
            float[] progressCoordinates = getProgressCoordinates();
            if (progressCoordinates.length > 0) {
                float[] textureCoordinates = progressToTextureCoordinates.apply(progressCoordinates);
                setBuffer(VertexBuffer.Type.TexCoord, 2, textureCoordinates);
            }
        }
    }

    protected abstract float[] getProgressCoordinates();

    private float[] getTextureCoordinates(float[] progressCoordinates, Vector2f start, Vector2f size) {
        float[] pixelCoordinates = new float[progressCoordinates.length];
        for (int i = 0; i < progressCoordinates.length; i += 2) {
            pixelCoordinates[i] = (start.getX() + (progressCoordinates[i] * size.getX()));
            pixelCoordinates[i + 1] = (start.getY() + (progressCoordinates[i + 1] * size.getY()));
        }
        return UvHelper.getUV_XY(pixelCoordinates, texture);
    }
}
