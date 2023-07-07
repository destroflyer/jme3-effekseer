package com.destroflyer.jme3.effekseer.virtual.renderer;

import com.destroflyer.jme3.effekseer.virtual.Util;
import com.destroflyer.jme3.effekseer.virtual.model.DrawingValues;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;

import java.util.LinkedList;

public abstract class CustomParticleMesh<EDV extends EffectiveDrawingValues, DV extends DrawingValues> extends ParticleMesh {

    public CustomParticleMesh(EffectiveUvValues effectiveUvValues, EDV effectiveDrawingValues, DV drawingValues) {
        super(effectiveUvValues);
        this.effectiveDrawingValues = effectiveDrawingValues;
        this.drawingValues = drawingValues;
    }
    protected EDV effectiveDrawingValues;
    protected DV drawingValues;
    protected LinkedList<Short> indices = new LinkedList<>();
    protected LinkedList<Float> positions = new LinkedList<>();
    protected LinkedList<Float> colors = new LinkedList<>();
    protected LinkedList<Float> progressCoordinates = new LinkedList<>();

    @Override
    public void initialize(Texture texture) {
        updateBuffers();
        super.initialize(texture);
    }

    public void updateBuffers() {
        positions.clear();
        indices.clear();
        colors.clear();
        progressCoordinates.clear();
        fillBuffers();
        setBuffer(Type.Index, 3, Util.convertToArray_Short(indices));
        setBuffer(Type.Position, 3, Util.convertToArray_Float(positions));
        setBuffer(Type.Color, 4, Util.convertToArray_Float(colors));
        updateBound();
        onModification();
    }

    protected abstract void fillBuffers();

    protected void addTriangle(Vector3f point1, Vector3f point2, Vector3f point3) {
        addPoint(point1);
        addPoint(point2);
        addPoint(point3);
    }

    private void addPoint(Vector3f point) {
        positions.add(point.getX());
        positions.add(point.getY());
        positions.add(point.getZ());
    }

    protected void addVertexColor(ColorRGBA color) {
        colors.add(color.getRed());
        colors.add(color.getGreen());
        colors.add(color.getBlue());
        colors.add(color.getAlpha());
    }

    protected void addProgressCoordinate(float x, float y) {
        progressCoordinates.add(x);
        progressCoordinates.add(y);
    }

    protected ColorRGBA getInterpolatedColor(ColorRGBA start, ColorRGBA end, float progress) {
        float red = getInterpolatedValue(start.getRed(), end.getRed(), progress);
        float green = getInterpolatedValue(start.getGreen(), end.getGreen(), progress);
        float blue = getInterpolatedValue(start.getBlue(), end.getBlue(), progress);
        float alpha = getInterpolatedValue(start.getAlpha(), end.getAlpha(), progress);
        return new ColorRGBA(red, green, blue, alpha);
    }

    protected float getInterpolatedValue(float start, float end, float progress) {
        return ((progress * (end - start)) + start);
    }

    public EDV getEffectiveDrawingValues() {
        return effectiveDrawingValues;
    }

    @Override
    protected float[] getProgressCoordinates() {
        return Util.convertToArray_Float(progressCoordinates);
    }
}
