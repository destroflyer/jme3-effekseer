package com.destroflyer.jme3.effekseer.virtual.renderer;

import com.destroflyer.jme3.effekseer.virtual.model.RibbonDrawingValues;
import com.jme3.math.*;
import com.jme3.util.TempVars;

import java.util.ArrayList;

public class RibbonMesh extends MergedParticlesMesh<EffectiveDrawingValues, RibbonDrawingValues> {

    public RibbonMesh(EffectiveUvValues effectiveUvValues, RibbonDrawingValues ribbonDrawingValues, ArrayList<Particle> particles) {
        super(effectiveUvValues, null, ribbonDrawingValues, particles);
    }

    @Override
    protected void setToothPosition1(Vector3f toothPosition, int segmentIndex) {
        setToothPosition(toothPosition, segmentIndex, drawingValues.getPositionLeftRight().getX());
    }

    @Override
    protected void setToothPosition2(Vector3f toothPosition, int segmentIndex) {
        setToothPosition(toothPosition, segmentIndex, drawingValues.getPositionLeftRight().getY());
    }

    private  void setToothPosition(Vector3f toothPosition, int segmentIndex, float position) {
        TempVars tempVars = TempVars.get();
        Vector3f toothRelativePosition = tempVars.vect1;
        toothRelativePosition.set(position, 0, 0);
        toothPosition.set(TransformHelper.getWorldLocation(particles.get(segmentIndex), toothRelativePosition));
        tempVars.release();
    }

    @Override
    protected ColorRGBA getVertexColor(int segmentIndex, float trackProgress, int toothSide) {
        EffectiveRibbonParticleDrawingValues effectiveRibbonParticleDrawingValues = (EffectiveRibbonParticleDrawingValues) particles.get(segmentIndex).getEffectiveDrawingValues();
        return effectiveRibbonParticleDrawingValues.getEffectiveColorValuesAll().getCurrentValue();
    }

    @Override
    protected float getProgressCoordinate_X(float progress, int toothSide) {
        return super.getProgressCoordinate_Y(progress, toothSide);
    }

    @Override
    protected float getProgressCoordinate_Y(float progress, int toothSide) {
        return super.getProgressCoordinate_X(progress, toothSide);
    }
}
