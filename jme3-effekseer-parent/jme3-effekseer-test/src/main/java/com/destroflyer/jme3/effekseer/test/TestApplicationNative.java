package com.destroflyer.jme3.effekseer.test;

import com.destroflyer.jme3.effekseer.nativ.Effekseer;
import com.destroflyer.jme3.effekseer.nativ.EffekseerControl;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

import java.util.function.Consumer;

public class TestApplicationNative extends SimpleApplication implements ActionListener {

    public static void main(String[] args) {
        TestApplicationNative testApplication = new TestApplicationNative();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("jme3-effekseer-native");
        settings.setWidth(1600);
        settings.setHeight(900);
        settings.setGammaCorrection(false);
        testApplication.setSettings(settings);
        testApplication.setShowSettings(false);
        testApplication.start();
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator(TestParticleEffects.ASSET_ROOT, FileLocator.class);
        Effekseer.initialize(stateManager, viewPort, assetManager, context.getSettings().isGammaCorrection());
        for (int i = 0; i < 2; i++) {
            spawn(i - 0.5f);
        }
        cam.setLocation(new Vector3f(0, 40, 200));
        inputManager.addMapping("spawn", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("clear", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("pause", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("play", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addListener(this, "spawn", "clear", "pause", "play");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("spawn") && isPressed) {
            spawn(FastMath.nextRandomFloat() - 0.5f);
        } else if (name.equals("clear") && isPressed) {
            rootNode.detachAllChildren();
        } else if (name.equals("pause") && isPressed) {
            forEachControl(EffekseerControl::pause);
        } else if (name.equals("play") && isPressed) {
            forEachControl(EffekseerControl::play);
        }
    }

    private void spawn(float x) {
        Node node = new Node();
        node.move(x * 100, 0, 0);
        EffekseerControl control = new EffekseerControl(assetManager, "samples/Pierre01/LightningStrike.efkefc");
        control.setSpeed(FastMath.nextRandomFloat());
        node.addControl(control);
        rootNode.attachChild(node);
    }

    private void forEachControl(Consumer<EffekseerControl> consumer) {
        for (Spatial spatial : rootNode.getChildren()) {
            EffekseerControl control = spatial.getControl(EffekseerControl.class);
            if (control != null) {
                consumer.accept(control);
            }
        }
    }
}
