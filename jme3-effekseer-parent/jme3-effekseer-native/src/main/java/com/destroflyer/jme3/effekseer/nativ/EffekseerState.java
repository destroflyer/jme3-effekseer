package com.destroflyer.jme3.effekseer.nativ;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EffekseerState extends AbstractAppState {

    public EffekseerState(ViewPort viewPort, boolean sRGB) {
        this.viewPort = viewPort;
        this.sRGB = sRGB;
    }
    private ViewPort viewPort;
    private boolean sRGB;
    @Getter
    private EffekseerManager manager;
    private List<EffekseerControl> controls = new ArrayList<>();
    private HashMap<Float, ArrayList<EffekseerControl>> tmpPlayingControlsGroupedBySpeed = new HashMap<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        manager = new EffekseerManager();
        manager.initialize(sRGB);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        for (EffekseerControl control : controls) {
            // Ensure the native objects are properly cleanuped before garbage collection kicks in (and crashes)
            if (viewPort.getScenes().stream().noneMatch(control::isChildOf)) {
                control.stop();
                manager.unregisterEmitter(control);
            }
        }
        controls.clear();
        for (Spatial scene : viewPort.getScenes()) {
            scene.depthFirstTraversal(spatial -> {
                for (int i = 0; i < spatial.getNumControls(); i++) {
                    if (spatial.getControl(i) instanceof EffekseerControl control) {
                        manager.registerEmitter(control);
                        control.setManager(manager);
                        controls.add(control);
                    }
                }
            });
        }
        updateManager(tpf);
    }

    // EffekseerManagerCore.update(tpf) only allows updating all effects with the same tpf
    // To allow different (potentially dynamic) speeds per effect, we can pause some effects and only update selected ones
    // To improve performance, we can group the effects by speed (instead of updating each one alone)
    private void updateManager(float tpf) {
        for (EffekseerControl control : controls) {
            if (control.isPlaying()) {
                tmpPlayingControlsGroupedBySpeed.computeIfAbsent(control.getSpeed(), s -> new ArrayList<>()).add(control);
            }
        }
        tmpPlayingControlsGroupedBySpeed.forEach((speed, controlsWithEqualSpeed) -> {
            for (EffekseerControl control : controls) {
                setEffectPaused(control, !controlsWithEqualSpeed.contains(control));
            }
            manager.update(tpf * speed);
        });
        if (tmpPlayingControlsGroupedBySpeed.isEmpty()) {
            manager.update(tpf);
        } else {
            tmpPlayingControlsGroupedBySpeed.clear();
        }
        for (EffekseerControl control : controls) {
            setEffectPaused(control, !control.isPlaying());
        }
    }

    private void setEffectPaused(EffekseerControl control, boolean paused) {
        control.getInstances().forEach(i -> manager.pauseEffect(i, paused));
    }

    @Override
    public void cleanup() {
        super.cleanup();
        manager.destroy();
    }
}
