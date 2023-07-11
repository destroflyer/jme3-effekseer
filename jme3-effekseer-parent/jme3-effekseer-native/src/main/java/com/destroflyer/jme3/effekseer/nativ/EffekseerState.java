package com.destroflyer.jme3.effekseer.nativ;

import com.jme3.app.state.AbstractAppState;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class EffekseerState extends AbstractAppState {

    public EffekseerState(ViewPort viewPort, boolean sRGB) {
        this.viewPort = viewPort;
        this.sRGB = sRGB;
        controls = new ArrayList<>();
    }
    private ViewPort viewPort;
    @Getter
    private boolean sRGB;
    @Getter
    private List<EffekseerControl> controls;

    @Override
    public void update(float tpf) {
        super.update(tpf);
        for (EffekseerControl control : controls) {
            // Ensure the native objects are properly cleanuped before garbage collection kicks in (and crashes)
            if (viewPort.getScenes().stream().noneMatch(control::isChildOf)) {
                control.destroy();
            }
        }
        controls.clear();
        for (Spatial scene : viewPort.getScenes()) {
            scene.depthFirstTraversal(spatial -> {
                EffekseerControl control = spatial.getControl(EffekseerControl.class);
                if (control != null) {
                    if (!control.isInitialized()) {
                        control.initialize(sRGB);
                    }
                    controls.add(control);
                }
            });
        }
    }
}
