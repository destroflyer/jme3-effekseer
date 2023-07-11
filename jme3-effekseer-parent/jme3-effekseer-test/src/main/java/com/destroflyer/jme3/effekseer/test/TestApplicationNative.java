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
import com.jme3.system.AppSettings;

import java.util.LinkedList;

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

    private LinkedList<Node> nodes = new LinkedList<>();

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator(TestParticleEffects.ASSET_ROOT, FileLocator.class);
        Effekseer.registerLoader(assetManager);
        Effekseer.initialize(stateManager, viewPort, assetManager, context.getSettings().isGammaCorrection());
        for (int i = 0; i < 2; i++) {
            spawn(i - 0.5f);
        }
        cam.setLocation(new Vector3f(0, 40, 200));
        inputManager.addMapping("spawn", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("clear", new KeyTrigger(KeyInput.KEY_DELETE));
        inputManager.addListener(this, "spawn", "clear");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("spawn") && isPressed) {
            spawn(FastMath.nextRandomFloat() - 0.5f);
        } else if (name.equals("clear") && isPressed) {
            for (Node node : nodes) {
                rootNode.detachChild(node);
            }
            nodes.clear();
        }
    }

    private void spawn(float x) {
        Node node = new Node();
        node.move(x * 100, 0, 0);
        EffekseerControl control = new EffekseerControl(assetManager, "samples/Pierre01/LightningStrike.efkefc");
        control.setSpeed(FastMath.nextRandomFloat());
        node.addControl(control);
        rootNode.attachChild(node);
        nodes.add(node);
    }
}
