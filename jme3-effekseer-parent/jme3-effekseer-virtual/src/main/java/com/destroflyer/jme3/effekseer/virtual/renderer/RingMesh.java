package com.destroflyer.jme3.effekseer.virtual.renderer;

import com.destroflyer.jme3.effekseer.virtual.model.RingDrawingValues;
import com.jme3.math.*;
import com.jme3.util.TempVars;

import static com.destroflyer.jme3.effekseer.virtual.renderer.DynamicValuesHelper.getCurrentLocalValue1f;
import static com.destroflyer.jme3.effekseer.virtual.renderer.DynamicValuesHelper.getCurrentLocalValue2f;

public class RingMesh extends ToothMesh<EffectiveRingDrawingValues, RingDrawingValues> {

    public RingMesh(EffectiveUvValues effectiveUvValues, EffectiveRingDrawingValues effectiveRingDrawingValues, RingDrawingValues ringDrawingValues) {
        super(effectiveUvValues, effectiveRingDrawingValues, ringDrawingValues);
    }
    private float segmentAngle;

    @Override
    protected int getToothCount() {
        return (drawingValues.getVertexCount() + 1);
    }

    @Override
    protected void prepareSegment(int segmentIndex) {
        float viewingAngle = getCurrentLocalValue1f(effectiveDrawingValues.getEffectiveViewingAngleValues()) * FastMath.DEG_TO_RAD;
        float angleOffset = (0 - (((FastMath.TWO_PI - viewingAngle) / 2)));
        segmentAngle = (angleOffset + (segmentIndex * (viewingAngle / (getToothCount() - 1))));
    }

    @Override
    protected void setToothPosition1(Vector3f toothPosition, int segmentIndex) {
        setToothPosition(toothPosition, effectiveDrawingValues.getEffectiveInnerPositionValues());
    }

    @Override
    protected void setToothPosition2(Vector3f toothPosition, int segmentIndex) {
        setToothPosition(toothPosition, effectiveDrawingValues.getEffectiveOuterPositionValues());
    }

    private void setToothPosition(Vector3f toothPosition, EffectiveDynamicValues<Vector2f> effectivePositionValues) {
        TempVars tempVars = TempVars.get();
        Vector2f positionValue = getCurrentLocalValue2f(effectivePositionValues);
        // X = Straight
        Vector3f distanceForward = tempVars.vect1;
        float x = FastMath.cos(segmentAngle);
        float y = FastMath.sin(segmentAngle);
        distanceForward.set(x, y, 0).multLocal(positionValue.getX());
        // Y = Sideways
        Vector3f distanceSideways = tempVars.vect2;
        distanceSideways.set(0, 0, positionValue.getY());
        toothPosition.set(distanceForward).addLocal(distanceSideways);
        tempVars.release();
    }

    @Override
    protected float getCenterRatio() {
        return getCurrentLocalValue1f(effectiveDrawingValues.getEffectiveCenterRatioValues());
    }

    @Override
    protected ColorRGBA getVertexColor(int segmentIndex, float progress, int toothSide) {
        if (toothSide == -1) {
            return effectiveDrawingValues.getEffectiveColorValuesOuter().getCurrentValue();
        } else if (toothSide == 1) {
            return effectiveDrawingValues.getEffectiveColorValuesInner().getCurrentValue();
        } else {
            return effectiveDrawingValues.getEffectiveColorValuesCenter().getCurrentValue();
        }
    }
}
