package com.destroflyer.jme3.effekseer.nativ;

import Effekseer.swig.EffekseerBackendCore;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.ViewPort;
import com.jme3.system.NativeLibraryLoader;
import com.jme3.system.Platform;

public class Effekseer {

    public static void registerLoader(AssetManager assetManager) {
        assetManager.registerLoader(EffekseerLoader.class, "efkefc");
    }

    public static void unregisterLoader(AssetManager assetManager) {
        assetManager.unregisterLoader(EffekseerLoader.class);
    }

    public static void initialize(AppStateManager stateManager, ViewPort viewPort, AssetManager assetManager, boolean sRGB) {
        initialize(stateManager, viewPort, assetManager, sRGB, false);
    }

    public static void initialize(AppStateManager stateManager, ViewPort viewPort, AssetManager assetManager, boolean sRGB, boolean isOrthographic) {
        initialize(stateManager, viewPort, assetManager, sRGB, isOrthographic, true);
    }

    public static void initialize(AppStateManager stateManager, ViewPort viewPort, AssetManager assetManager, boolean sRGB, boolean isOrthographic, boolean hasDepth) {
        initalizeNativeLibrary();
        EffekseerState effekseerState = new EffekseerState(viewPort, sRGB);
        stateManager.attach(effekseerState);
        viewPort.addProcessor(new EffekseerProcessor(effekseerState, assetManager, sRGB, isOrthographic, hasDepth));
    }

    private static void initalizeNativeLibrary() {
        NativeLibraryLoader.registerNativeLibrary("effekseer", Platform.Linux64, "native/linux/x86_64/libEffekseerNativeForJava.so");
        NativeLibraryLoader.registerNativeLibrary("effekseer", Platform.Windows64, "native/windows/x86_64/EffekseerNativeForJava.dll");
        NativeLibraryLoader.loadNativeLibrary("effekseer", true);
        EffekseerBackendCore.InitializeAsOpenGL();
    }

    public static void destroy() {
        EffekseerBackendCore.Terminate();
    }
}
