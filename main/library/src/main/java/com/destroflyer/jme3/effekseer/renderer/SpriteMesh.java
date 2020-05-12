package com.destroflyer.jme3.effekseer.renderer;

import com.destroflyer.jme3.effekseer.model.SpriteVertexPositions;
import com.jme3.scene.VertexBuffer;

public class SpriteMesh extends ParticleMesh {

    public SpriteMesh(EffectiveUvValues effectiveUvValues, SpriteVertexPositions spriteVertexPositions) {
        super(effectiveUvValues);
        setBuffer(VertexBuffer.Type.Index, 3, new short[] {
            0, 1, 2,
            0, 2, 3
        });
        setBuffer(VertexBuffer.Type.Position, 3, getVertexPositions(spriteVertexPositions));
        setBuffer(VertexBuffer.Type.Normal, 3, new float[] {
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1
        });
        updateBound();
    }
    private static final float[] PROGRESS_COORDINATES = new float[] {
        0, 1,
        1, 1,
        1, 0,
        0, 0
    };

    private float[] getVertexPositions(SpriteVertexPositions spriteVertexPositions) {
        return new float[] {
            spriteVertexPositions.getBottomLeft().getX(), spriteVertexPositions.getBottomLeft().getY(), 0,
            spriteVertexPositions.getBottomRight().getX(), spriteVertexPositions.getBottomRight().getY(), 0,
            spriteVertexPositions.getTopRight().getX(), spriteVertexPositions.getTopRight().getY(), 0,
            spriteVertexPositions.getTopLeft().getX(), spriteVertexPositions.getTopLeft().getY(), 0,
        };
    }

    @Override
    protected float[] getProgressCoordinates() {
        return PROGRESS_COORDINATES;
    }
}
