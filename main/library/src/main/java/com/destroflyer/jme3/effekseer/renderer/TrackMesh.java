package com.destroflyer.jme3.effekseer.renderer;

import com.destroflyer.jme3.effekseer.model.TrackDrawingValues;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.util.TempVars;

import java.util.ArrayList;

public class TrackMesh extends MergedParticlesMesh<EffectiveTrackDrawingValues, TrackDrawingValues> {

    public TrackMesh(EffectiveUvValues effectiveUvValues, EffectiveTrackDrawingValues effectiveTrackDrawingValues, TrackDrawingValues trackDrawingValues, ArrayList<Particle> particles) {
        super(effectiveUvValues, effectiveTrackDrawingValues, trackDrawingValues, particles);
    }

    @Override
    protected void setToothPosition1(Vector3f toothPosition, int segmentIndex) {
        setToothPosition(toothPosition, segmentIndex, true);
    }

    @Override
    protected void setToothPosition2(Vector3f toothPosition, int segmentIndex) {
        setToothPosition(toothPosition, segmentIndex, false);
    }

    private  void setToothPosition(Vector3f toothPosition, int index, boolean upOrDown) {
        float trackProgress = (((float) index) / (getToothCount() - 1));
        float toothLength = getToothLength(trackProgress);

        TempVars tempVars = TempVars.get();
        Vector3f trackDirection = tempVars.vect1;
        Vector3f toothDirection = tempVars.vect2;
        trackDirection.set(nextSegmentPosition).subtractLocal(segmentPosition).normalizeLocal();
        toothDirection.set(trackDirection).crossLocal(cameraDirection).normalizeLocal().multLocal(toothLength);
        toothPosition.set(segmentPosition);
        if (upOrDown) {
            toothPosition.addLocal(toothDirection);
        } else {
            toothPosition.subtractLocal(toothDirection);
        }
        tempVars.release();
    }

    private float getToothLength(float trackProgress) {
        float interpolationProgress;
        float interpolationStart;
        float interpolationEnd;
        if (trackProgress < 0.5f) {
            interpolationProgress = (trackProgress * 2);
            interpolationStart = drawingValues.getSizeFrontFixed();
            interpolationEnd = drawingValues.getSizeMiddleFixed();
        } else {
            interpolationProgress = ((trackProgress - 0.5f) * 2);
            interpolationStart = drawingValues.getSizeMiddleFixed();
            interpolationEnd = drawingValues.getSizeBackFixed();
        }
        return (getInterpolatedValue(interpolationStart, interpolationEnd, interpolationProgress) / 2);
    }

    @Override
    protected ColorRGBA getVertexColor(int segmentIndex, float trackProgress, int toothSide) {
        float interpolationProgress;
        if (trackProgress < 0.5f) {
            interpolationProgress = (trackProgress * 2);
        } else {
            interpolationProgress = (1 - ((trackProgress - 0.5f) * 2));
        }
        ColorRGBA interpolationStart;
        ColorRGBA interpolationEnd;
        if (toothSide == -1) {
            interpolationStart = effectiveDrawingValues.getEffectiveColorValuesRightFixed().getCurrentValue();
            interpolationEnd = effectiveDrawingValues.getEffectiveColorValuesRightCenterFixed().getCurrentValue();
        } else if (toothSide == 1) {
            interpolationStart = effectiveDrawingValues.getEffectiveColorValuesLeftFixed().getCurrentValue();
            interpolationEnd = effectiveDrawingValues.getEffectiveColorValuesLeftCenterFixed().getCurrentValue();
        } else {
            interpolationStart = effectiveDrawingValues.getEffectiveColorValuesCenterFixed().getCurrentValue();
            interpolationEnd = effectiveDrawingValues.getEffectiveColorValuesCenterMiddleFixed().getCurrentValue();
        }
        return getInterpolatedColor(interpolationStart, interpolationEnd, interpolationProgress);
    }
}
