package com.destroflyer.jme3.effekseer.virtual.reader;

import com.destroflyer.jme3.effekseer.virtual.model.*;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EffekseerReader {

    private static final Vector2f DEFAULT_RING_POSITION_OUTER = new Vector2f(2, 0);
    private static final Vector2f DEFAULT_RING_POSITION_INNER = new Vector2f(1, 0);
    private static final Vector2f DEFAULT_RIBBON_POSITION = new Vector2f(-0.5f, 0.5f);

    public ParticleEffect read(String assetRoot, String filePath) {
        return read(assetRoot, new File(filePath));
    }

    public ParticleEffect read(String assetRoot, File file) {
        try {
            Document document = new SAXBuilder().build(file);
            return parseParticleEffect(getRelativizedParentDirectory(assetRoot, file), document.getRootElement());
        } catch (JDOMException | IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getRelativizedParentDirectory(String assetRoot, File file) {
        return new File(assetRoot).toURI().relativize(file.getParentFile().toURI()).getPath();
    }

    public ParticleEffect parseParticleEffect(String directory, Element element) {
        RootNode root = parseRootNode(element.getChild("Root"));
        int startFrame = EffekseerBasics.parseInteger(element.getChild("StartFrame"), 0);
        int endFrame = EffekseerBasics.parseInteger(element.getChild("EndFrame"), 120);
        boolean isLoop = EffekseerBasics.parseBoolean(element.getChild("IsLoop"), true);

        return ParticleEffect.builder()
                .directory(directory)
                .root(root)
                .startFrame(startFrame)
                .endFrame(endFrame)
                .isLoop(isLoop)
                .build();
    }

    private RootNode parseRootNode(Element element) {
        String name = element.getChildText("Name");
        boolean rendered = EffekseerBasics.parseBoolean(element.getChild("IsRendered"), true);
        List<ParticleNode> children = element.getChild("Children").getChildren("Node").stream()
                .map(this::parseParticleNode)
                .collect(Collectors.toList());

        return RootNode.builder()
                .name(name)
                .children(children)
                .rendered(rendered)
                .build();
    }

    private ParticleNode parseParticleNode(Element element) {
        String name = element.getChildText("Name");
        boolean rendered = EffekseerBasics.parseBoolean(element.getChild("IsRendered"), true);
        List<ParticleNode> children = element.getChild("Children").getChildren("Node").stream()
                .map(this::parseParticleNode)
                .collect(Collectors.toList());

        CommonValues commonValues = parseCommonValues(element.getChild("CommonValues"));
        DynamicValues<Range3f> locationValues = EffekseerHelper.parseDynamicValues_Nested(element.getChild("LocationValues"), "Location", Vector3f.ZERO, Range3f.class);
        DynamicValues<Range3f> rotationValues = EffekseerHelper.parseDynamicValues_Nested(element.getChild("RotationValues"), "Rotation", Vector3f.ZERO, Range3f.class);
        DynamicValues<Range3f> scalingValues = EffekseerHelper.parseDynamicValues_Nested(element.getChild("ScalingValues"), "Scale", Vector3f.UNIT_XYZ, Range3f.class);
        RendererCommonValues rendererCommonValues = parseRendererCommonValues(element.getChild("RendererCommonValues"));
        DrawingValues drawingValues = parseDrawingValues(element.getChild("DrawingValues"));

        return ParticleNode.builder()
                .name(name)
                .children(children)
                .rendered(rendered)
                .commonValues(commonValues)
                .locationValues(locationValues)
                .rotationValues(rotationValues)
                .scalingValues(scalingValues)
                .rendererCommonValues(rendererCommonValues)
                .drawingValues(drawingValues)
                .build();
    }

    private CommonValues parseCommonValues(Element element) {
        MaxGeneration maxGeneration = parseMaxGeneration(element);
        Range1f life = EffekseerBasics.parseRange1f(element, "Life", 100);
        TransformInheritance locationInheritance = parseTransformInheritance(element, "Location");
        TransformInheritance rotationInheritance = parseTransformInheritance(element, "Rotation");
        TransformInheritance scalingInheritance = parseTransformInheritance(element, "Scale");
        boolean destroyAfterTime = EffekseerBasics.parseBoolean(element, "RemoveWhenLifeIsExtinct", true);
        Range1f generationTimeOffset = EffekseerBasics.parseRange1f(element, "GenerationTimeOffset", 0);
        Range1f generationTime = EffekseerBasics.parseRange1f(element, "GenerationTime", 1);

        return CommonValues.builder()
                .maxGeneration(maxGeneration)
                .life(life)
                .locationInheritance(locationInheritance)
                .rotationInheritance(rotationInheritance)
                .scalingInheritance(scalingInheritance)
                .destroyAfterTime(destroyAfterTime)
                .generationTimeOffset(generationTimeOffset)
                .generationTime(generationTime)
                .build();
    }

    private MaxGeneration parseMaxGeneration(Element commonValuesElement) {
        int value = 1;
        boolean infinite = false;
        if (commonValuesElement != null) {
            Element element = commonValuesElement.getChild("MaxGeneration");
            if (element != null) {
                value = EffekseerBasics.parseInteger(element.getChild("Value"), value);
                infinite = EffekseerBasics.parseBoolean(element.getChild("Infinite"), infinite);
            }
        }
        return MaxGeneration.builder()
                .value(value)
                .infinite(infinite)
                .build();
    }

    private TransformInheritance parseTransformInheritance(Element commonValuesElement, String elementNameBase) {
        int type = 2;
        if (commonValuesElement != null) {
            type = EffekseerBasics.parseInteger(commonValuesElement.getChild(elementNameBase + "EffectType"), type);
        }
        switch (type) {
            case 0: return TransformInheritance.NEVER;
            case 1: return TransformInheritance.ONLY_ON_CREATE;
            case 2: return TransformInheritance.ALWAYS;
            // "Root dependent (deprecated)"
            case 3: return TransformInheritance.NEVER;
        }
        return null;
    }

    private RendererCommonValues parseRendererCommonValues(Element element) {
        String colorTexture = null;
        int alphaBlend = 1;
        boolean zWrite = false;
        boolean zTest = true;
        float distortionIntensity = 0;
        FadeValues fadeIn = parseFadeValues(element, "FadeIn");
        FadeValues fadeOut = parseFadeValues(element, "FadeOut");
        UvValues uvValues = parseUvValues(element, "UV");
        if (element != null) {
            colorTexture = element.getChildText("ColorTexture");
            alphaBlend = EffekseerBasics.parseInteger(element.getChild("AlphaBlend"), alphaBlend);
            zWrite = EffekseerBasics.parseBoolean(element.getChild("ZWrite"), zWrite);
            zTest = EffekseerBasics.parseBoolean(element.getChild("ZTest"), zTest);
            boolean isDistortion = EffekseerBasics.parseBoolean(element.getChild("Distortion"), false);
            if (isDistortion) {
                distortionIntensity = EffekseerBasics.parseFloat(element.getChild("DistortionIntensity"), 1);
            }
        }

        return RendererCommonValues.builder()
                .colorTexture(colorTexture)
                .alphaBlend(alphaBlend)
                .zWrite(zWrite)
                .zTest(zTest)
                .fadeIn(fadeIn)
                .fadeOut(fadeOut)
                .uvValues(uvValues)
                .distortionIntensity(distortionIntensity)
                .build();
    }

    private FadeValues parseFadeValues(Element rendererCommonValuesElement, String elementNameBase) {
        if (rendererCommonValuesElement != null) {
            int type = EffekseerBasics.parseInteger(rendererCommonValuesElement.getChild(elementNameBase + "Type"), 0);
            if (type == 1) {
                Element element = rendererCommonValuesElement.getChild(elementNameBase);
                float duration = EffekseerBasics.parseFloat(element, "Frame", 1);

                return FadeValues.builder()
                        .duration(duration)
                        .build();
            }
        }
        return null;
    }

    private UvValues parseUvValues(Element rendererCommonValuesElement, String elementNameBase) {
        int type = EffekseerBasics.parseInteger(rendererCommonValuesElement, elementNameBase, 0);
        switch (type) {
            case 0:
                return new StandardUvValues();
            case 1:
                Element fixed = rendererCommonValuesElement.getChild(elementNameBase + "Fixed");
                Vector2f start1 = EffekseerBasics.parseVector2f(fixed, "Start", Vector2f.ZERO);
                Vector2f size1 = EffekseerBasics.parseVector2f(fixed, "Size", Vector2f.ZERO);

                return FixedUvValues.builder()
                        .start(start1)
                        .size(size1)
                        .build();
            case 3:
                Element scroll = rendererCommonValuesElement.getChild(elementNameBase + "Scroll");
                Range2f start3 = EffekseerHelper.parseRange2f(scroll, "Start", Vector2f.ZERO);
                Range2f size3 = EffekseerHelper.parseRange2f(scroll, "Size", Vector2f.ZERO);
                Range2f speed3 = EffekseerHelper.parseRange2f(scroll, "Speed", Vector2f.ZERO);

                return ScrollUvValues.builder()
                        .start(start3)
                        .size(size3)
                        .speed(speed3)
                        .build();
        }
        return null;
    }

    private DrawingValues parseDrawingValues(Element element) {
        int type = EffekseerBasics.parseInteger(element, "Type", 2);
        switch (type) {
            case 2:
                Element sprite = ((element != null) ? element.getChild("Sprite") : null);
                int billboard2 = EffekseerBasics.parseInteger(sprite, "Billboard", 0);
                ColorValues colorValues2 = EffekseerColorParser.parseColorValues(sprite, "ColorAll", 255);
                SpriteVertexPositions vertexPositions2 = parseSpriteVertexPositions(sprite);

                return SpriteDrawingValues.builder()
                        .billboard(billboard2)
                        .colorValues(colorValues2)
                        .vertexPositions(vertexPositions2)
                        .build();
            case 3:
                Element ribbon = element.getChild("Ribbon");
                Vector2f positionLeftRight3 = parseRibbonPositionLeftRight(ribbon);
                ColorValues colorValuesAll3 = EffekseerColorParser.parseColorValues(ribbon, "ColorAll", 255);

                return RibbonDrawingValues.builder()
                        .positionLeftRight(positionLeftRight3)
                        .particleDrawingValues(RibbonParticleDrawingValues.builder()
                                .colorValuesAll(colorValuesAll3)
                                .build())
                        .build();
            case 4:
                Element ring = element.getChild("Ring");
                int billboard4 = EffekseerBasics.parseInteger(ring, "Billboard", 2);
                int vertexCount4 = EffekseerBasics.parseInteger(ring, "VertexCount", 16);
                DynamicValues<Range1f> viewingAngleValues4 = EffekseerHelper.parseDynamicValues_Flat(ring, "ViewingAngle", null, 360f, Range1f.class);
                DynamicValues<Range2f> innerPositionValues4 = EffekseerHelper.parseDynamicValues_Flat(ring, "Inner", "Location", DEFAULT_RING_POSITION_INNER, Range2f.class);
                DynamicValues<Range2f> outerPositionValues4 = EffekseerHelper.parseDynamicValues_Flat(ring, "Outer", "Location", DEFAULT_RING_POSITION_OUTER, Range2f.class);
                DynamicValues<Range1f> centerRatioValues4 = EffekseerHelper.parseDynamicValues_Flat(ring, "CenterRatio", null, 0.5f, Range1f.class);
                ColorValues colorValuesInner4 = EffekseerColorParser.parseColorValues(ring, "InnerColor", 0);
                ColorValues colorValuesCenter4 = EffekseerColorParser.parseColorValues(ring, "CenterColor", 255);
                ColorValues colorValuesOuter4 = EffekseerColorParser.parseColorValues(ring, "OuterColor", 0);

                return RingDrawingValues.builder()
                        .billboard(billboard4)
                        .vertexCount(vertexCount4)
                        .viewingAngleValues(viewingAngleValues4)
                        .innerPositionValues(innerPositionValues4)
                        .outerPositionValues(outerPositionValues4)
                        .centerRatioValues(centerRatioValues4)
                        .colorValuesInner(colorValuesInner4)
                        .colorValuesCenter(colorValuesCenter4)
                        .colorValuesOuter(colorValuesOuter4)
                        .build();
            case 6:
                Element track = element.getChild("Track");
                float sizeFrontFixed6 = EffekseerBasics.parseFloat(track, "TrackSizeFor_Fixed", 1);
                float sizeMiddleFixed6 = EffekseerBasics.parseFloat(track, "TrackSizeMiddle_Fixed", 1);
                float sizeBackFixed6 = EffekseerBasics.parseFloat(track, "TrackSizeBack_Fixed", 1);
                ColorValues colorValuesLeft6 = EffekseerColorParser.parseColorValues(track, "ColorLeft", 255);
                ColorValues colorValuesLeftCenter6 = EffekseerColorParser.parseColorValues(track, "ColorLeftMiddle", 255);
                ColorValues colorValuesCenter6 = EffekseerColorParser.parseColorValues(track, "ColorCenter", 255);
                ColorValues colorValuesCenterMiddle6 = EffekseerColorParser.parseColorValues(track, "ColorCenterMiddle", 255);
                ColorValues colorValuesRight6 = EffekseerColorParser.parseColorValues(track, "ColorRight", 255);
                ColorValues colorValuesRightCenter6 = EffekseerColorParser.parseColorValues(track, "ColorRightMiddle", 255);

                return TrackDrawingValues.builder()
                        .sizeFrontFixed(sizeFrontFixed6)
                        .sizeMiddleFixed(sizeMiddleFixed6)
                        .sizeBackFixed(sizeBackFixed6)
                        .colorValuesLeft(colorValuesLeft6)
                        .colorValuesLeftCenter(colorValuesLeftCenter6)
                        .colorValuesCenter(colorValuesCenter6)
                        .colorValuesCenterMiddle(colorValuesCenterMiddle6)
                        .colorValuesRight(colorValuesRight6)
                        .colorValuesRightCenter(colorValuesRightCenter6)
                        .build();
        }
        return null;
    }

    private Vector2f parseRibbonPositionLeftRight(Element ribbonElement) {
        int type = EffekseerBasics.parseInteger(ribbonElement, "Position", 0);
        switch (type) {
            case 0:
                return DEFAULT_RIBBON_POSITION;
            case 1:
                float left = EffekseerBasics.parseFloat(ribbonElement.getChild("Position_Fixed_L"), DEFAULT_RIBBON_POSITION.getX());
                float right = EffekseerBasics.parseFloat(ribbonElement.getChild("Position_Fixed_R"), DEFAULT_RIBBON_POSITION.getY());
                return new Vector2f(left, right);
        }
        return null;
    }

    private SpriteVertexPositions parseSpriteVertexPositions(Element sprite) {
        Vector2f topLeft = new Vector2f(-0.5f, 0.5f);
        Vector2f topRight = new Vector2f(0.5f, 0.5f);
        Vector2f bottomLeft = new Vector2f(-0.5f, -0.5f);
        Vector2f bottomRight = new Vector2f(0.5f, -0.5f);
        int positionType = EffekseerBasics.parseInteger(sprite, "Position", 0);
        if (positionType == 1) {
            Element fixedTopLeft = sprite.getChild("Position_Fixed_UL");
            topLeft.setX(EffekseerBasics.parseFloat(fixedTopLeft, "X", topLeft.getX()));
            topLeft.setY(EffekseerBasics.parseFloat(fixedTopLeft, "Y", topLeft.getY()));

            Element fixedTopRight = sprite.getChild("Position_Fixed_UR");
            topRight.setX(EffekseerBasics.parseFloat(fixedTopRight, "X", topRight.getX()));
            topRight.setY(EffekseerBasics.parseFloat(fixedTopRight, "Y", topRight.getY()));

            Element fixedBottomLeft = sprite.getChild("Position_Fixed_LL");
            bottomLeft.setX(EffekseerBasics.parseFloat(fixedBottomLeft, "X", bottomLeft.getX()));
            bottomLeft.setY(EffekseerBasics.parseFloat(fixedBottomLeft, "Y", bottomLeft.getY()));

            Element fixedBottomRight = sprite.getChild("Position_Fixed_LR");
            bottomRight.setX(EffekseerBasics.parseFloat(fixedBottomRight, "X", bottomRight.getX()));
            bottomRight.setY(EffekseerBasics.parseFloat(fixedBottomRight, "Y", bottomRight.getY()));
        }
        return SpriteVertexPositions.builder()
                .topLeft(topLeft)
                .topRight(topRight)
                .bottomLeft(bottomLeft)
                .bottomRight(bottomRight)
                .build();
    }
}
