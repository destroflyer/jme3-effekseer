package com.destroflyer.jme3.effekseer.renderer;

import com.destroflyer.jme3.effekseer.model.DrawingValues;
import com.jme3.math.Vector3f;
import com.jme3.util.TempVars;

import java.util.ArrayList;

public abstract class MergedParticlesMesh<EDV extends EffectiveDrawingValues, DV extends DrawingValues> extends ToothMesh<EDV, DV> {

    public MergedParticlesMesh(EffectiveUvValues effectiveUvValues, EDV effectiveDrawingValues, DV drawingValues, ArrayList<Particle> particles) {
        super(effectiveUvValues, effectiveDrawingValues, drawingValues);
        this.particles = particles;
    }
    protected ArrayList<Particle> particles;
    protected Vector3f segmentPosition = new Vector3f();
    protected Vector3f nextSegmentPosition = new Vector3f();
    protected Vector3f cameraDirection;

    @Override
    protected int getToothCount() {
        return particles.size();
    }

    @Override
    protected void prepareSegment(int segmentIndex) {
        segmentPosition.set(getSegmentPosition(segmentIndex));
        if (segmentIndex == (particles.size() - 1)) {
            // For the last segment, assume the next segment would just continue in the same direction
            nextSegmentPosition.set(segmentPosition);
            if (segmentIndex > 0) {
                TempVars tempVars = TempVars.get();
                Vector3f previousSegmentDirection = tempVars.vect1;
                previousSegmentDirection.set(segmentPosition).subtractLocal(getSegmentPosition(segmentIndex - 1));
                nextSegmentPosition.addLocal(previousSegmentDirection);
                tempVars.release();
            }
        } else {
            nextSegmentPosition.set(getSegmentPosition(segmentIndex + 1));
        }
    }

    private Vector3f getSegmentPosition(int segmentIndex) {
        Particle particle = particles.get(segmentIndex);
        return particle.getTransform().getTranslation();
    }

    void setCameraDirection(Vector3f cameraDirection) {
        this.cameraDirection = cameraDirection;
    }
}
