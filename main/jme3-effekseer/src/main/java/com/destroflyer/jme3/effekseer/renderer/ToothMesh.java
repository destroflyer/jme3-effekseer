package com.destroflyer.jme3.effekseer.renderer;

import com.destroflyer.jme3.effekseer.model.DrawingValues;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

public abstract class ToothMesh<EDV extends EffectiveDrawingValues, DV extends DrawingValues> extends CustomParticleMesh<EDV, DV> {

    public ToothMesh(EffectiveUvValues effectiveUvValues, EDV effectiveDrawingValues, DV drawingValues) {
        super(effectiveUvValues, effectiveDrawingValues, drawingValues);
    }

    @Override
    public void fillBuffers() {
        short currentIndex = 0;
        int toothCount = getToothCount();
        float centerRatio = getCenterRatio();
        // Each segment has 4 triangles (2 per tooth)
        for (int i = 0; i < (toothCount * 4); i++) {
            indices.add((short) (currentIndex + 1));
            indices.add(currentIndex);
            indices.add((short) (currentIndex + 2));
            currentIndex += 3;
        }
        Vector3f centerPosition = new Vector3f();
        Vector3f tooth1 = new Vector3f();
        Vector3f tooth2 = new Vector3f();
        Vector3f lastCenterPosition = new Vector3f();
        Vector3f lastTooth1 = new Vector3f();
        Vector3f lastTooth2 = new Vector3f();
        for (int i = 0; i < toothCount; i++) {
            prepareSegment(i);
            setToothPosition1(tooth1, i);
            setToothPosition2(tooth2, i);
            centerPosition.set(tooth1).interpolateLocal(tooth2, centerRatio);

            if (i > 0) {
                addTooth(lastCenterPosition, lastTooth1, i, centerPosition, tooth1, 1);
                addTooth(lastCenterPosition, lastTooth2, i, centerPosition, tooth2, -1);
            }

            lastCenterPosition.set(centerPosition);
            lastTooth1.set(tooth1);
            lastTooth2.set(tooth2);
        }
    }

    protected abstract int getToothCount();

    protected abstract void prepareSegment(int segmentIndex);

    protected abstract void setToothPosition1(Vector3f toothPosition, int segmentIndex);

    protected abstract void setToothPosition2(Vector3f toothPosition, int segmentIndex);

    protected float getCenterRatio() {
        return 0.5f;
    }

    private void addTooth(
            Vector3f lastCenterPosition, Vector3f lastTooth,
            int segmentIndex, Vector3f centerPosition, Vector3f tooth, int toothSide
    ) {
        addTriangle(lastCenterPosition, lastTooth, tooth);
        addAdditionalInformation((segmentIndex - 1), 0);
        addAdditionalInformation((segmentIndex - 1), toothSide);
        addAdditionalInformation(segmentIndex, toothSide);

        addTriangle(lastCenterPosition, tooth, centerPosition);
        addAdditionalInformation((segmentIndex - 1), 0);
        addAdditionalInformation(segmentIndex, toothSide);
        addAdditionalInformation(segmentIndex, 0);
    }

    private void addAdditionalInformation(int segmentIndex, int toothSide) {
        float progress = (((float) segmentIndex) / (getToothCount() - 1));
        addVertexColor(getVertexColor(segmentIndex, progress, toothSide));
        float progressCoordinateX = getProgressCoordinate_X(progress, toothSide);
        float progressCoordinateY = getProgressCoordinate_Y(progress, toothSide);
        addProgressCoordinate(progressCoordinateX, progressCoordinateY);
    }

    protected abstract ColorRGBA getVertexColor(int segmentIndex, float progress, int toothSide);

    protected float getProgressCoordinate_X(float progress, int toothSide) {
        return progress;
    }

    protected float getProgressCoordinate_Y(float progress, int toothSide) {
        return (1 - ((toothSide + 1f) / 2));
    }
}
