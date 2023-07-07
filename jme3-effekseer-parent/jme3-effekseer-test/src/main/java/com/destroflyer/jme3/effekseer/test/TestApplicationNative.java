package com.destroflyer.jme3.effekseer.test;

import com.destroflyer.jme3.effekseer.nativ.EffekseerEmitterControl;
import com.destroflyer.jme3.effekseer.nativ.jme3.EffekseerRenderer;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

public class TestApplicationNative extends SimpleApplication {

    public static void main(String[] args) {
        TestApplicationNative testApplication = new TestApplicationNative();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("jme3-effekseer-native");
        settings.setWidth(1600);
        settings.setHeight(900);
        testApplication.setSettings(settings);
        testApplication.setShowSettings(false);
        testApplication.start();
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator(TestParticleEffects.ASSET_ROOT, FileLocator.class);
        EffekseerRenderer.addToViewPort(stateManager, viewPort, assetManager, context.getSettings().isGammaCorrection());
        rootNode.addControl(new EffekseerEmitterControl(assetManager, "samples/Pierre01/LightningStrike.efkefc"));
        cam.setLocation(new Vector3f(0, 13, 90));
    }
}
