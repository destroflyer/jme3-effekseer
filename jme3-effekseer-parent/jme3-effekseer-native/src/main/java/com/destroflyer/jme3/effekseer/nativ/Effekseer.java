package com.destroflyer.jme3.effekseer.nativ;

import Effekseer.swig.EffekseerBackendCore;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.ViewPort;
import com.jme3.system.NativeLibraryLoader;
import com.jme3.system.Platform;

public class Effekseer {

    public static void initialize(AppStateManager stateManager, ViewPort viewPort, AssetManager assetManager, boolean sRGB) {
        initialize(stateManager, viewPort, assetManager, sRGB, false);
    }

    public static void initialize(AppStateManager stateManager, ViewPort viewPort, AssetManager assetManager, boolean sRGB, boolean isOrthographic) {
        initialize(stateManager, viewPort, assetManager, sRGB, isOrthographic, true);
    }

    public static void initialize(AppStateManager stateManager, ViewPort viewPort, AssetManager assetManager, boolean sRGB, boolean isOrthographic, boolean hasDepth) {
        NativeLibraryLoader.registerNativeLibrary("effekseer", Platform.Linux64, "native/linux/x86_64/libEffekseerNativeForJava.so");
        NativeLibraryLoader.registerNativeLibrary("effekseer", Platform.Windows64, "native/windows/x86_64/EffekseerNativeForJava.dll");
        NativeLibraryLoader.loadNativeLibrary("effekseer", true);
        EffekseerBackendCore.InitializeAsOpenGL();

        assetManager.registerLoader(EffekseerLoader.class, "efkefc");

        EffekseerState effekseerState = new EffekseerState(viewPort, sRGB);
        stateManager.attach(effekseerState);
        viewPort.addProcessor(new EffekseerProcessor(effekseerState, assetManager, sRGB, isOrthographic, hasDepth));
    }

    public static void unregister(AssetManager assetManager) {
        assetManager.unregisterLoader(EffekseerLoader.class);
        EffekseerBackendCore.Terminate();
    }
}
