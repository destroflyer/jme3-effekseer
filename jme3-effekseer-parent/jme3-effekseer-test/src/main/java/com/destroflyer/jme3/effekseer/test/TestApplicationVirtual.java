package com.destroflyer.jme3.effekseer.test;

import com.destroflyer.jme3.effekseer.virtual.model.ParticleEffectSettings;
import com.destroflyer.jme3.effekseer.virtual.renderer.EffekseerControl;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

import java.util.ArrayList;

public class TestApplicationVirtual extends SimpleApplication {

    public static void main(String[] args) {
        TestApplicationVirtual testApplication = new TestApplicationVirtual();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("jme3-effekseer-virtual");
        settings.setWidth(1600);
        settings.setHeight(900);
        testApplication.setSettings(settings);
        testApplication.setShowSettings(false);
        testApplication.start();
    }
    private ArrayList<TestParticleEffect> testParticleEffects;
    private int currentParticleEffectIndex;
    private float cameraAngle;
    private float cameraDistance;
    private float cameraHeight;
    private float cameraSpeed;
    private float totalPassedTime = 0;

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator(TestParticleEffects.ASSET_ROOT, FileLocator.class);
        stateManager.detach(stateManager.getState(StatsAppState.class));
        testParticleEffects = new TestParticleEffects().getTestParticleEffects(2);
        currentParticleEffectIndex = -1;
        flyCam.setEnabled(false);
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        totalPassedTime += tpf;
        if (totalPassedTime > 1) {
            if ((currentParticleEffectIndex < testParticleEffects.size()) && (rootNode.getControl(EffekseerControl.class) == null)) {
                playNextParticleEffect();
            }
            updateCameraAngle(tpf);
        }
    }

    private void playNextParticleEffect() {
        currentParticleEffectIndex++;
        if (currentParticleEffectIndex < testParticleEffects.size()) {
            TestParticleEffect newTestParticleEffect = testParticleEffects.get(currentParticleEffectIndex);
            TestParticleEffectInfo testParticleEffectInfo = newTestParticleEffect.getInfo();
            System.out.println("Playing " + newTestParticleEffect.getFileName());
            ParticleEffectSettings particleEffectSettings = ParticleEffectSettings.builder()
                    .frameLength(testParticleEffectInfo.getFrameLength())
                    .loop(false)
                    .build();
            rootNode.addControl(new EffekseerControl(newTestParticleEffect.getParticleEffect(), particleEffectSettings, assetManager));
            cameraAngle = testParticleEffectInfo.getCameraAngle();
            cameraDistance = testParticleEffectInfo.getCameraDistance();
            cameraHeight = testParticleEffectInfo.getCameraHeight();
            cameraSpeed = testParticleEffectInfo.getCameraSpeed();
        }
    }

    private void updateCameraAngle(float tpf) {
        cameraAngle += ((cameraSpeed * FastMath.TWO_PI) * tpf);
        float cameraX = (FastMath.sin(cameraAngle) * cameraDistance);
        float cameraZ = (FastMath.cos(cameraAngle) * cameraDistance);
        cam.setLocation(new Vector3f(cameraX, cameraHeight, cameraZ));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
}
