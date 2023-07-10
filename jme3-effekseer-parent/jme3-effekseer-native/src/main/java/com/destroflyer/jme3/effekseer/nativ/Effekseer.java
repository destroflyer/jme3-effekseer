package com.destroflyer.jme3.effekseer.nativ;

import Effekseer.swig.EffekseerBackendCore;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.ViewPort;
import com.jme3.system.NativeLibraryLoader;
import com.jme3.system.Platform;

public class Effekseer {

    static {
        NativeLibraryLoader.registerNativeLibrary("effekseer", Platform.Linux64, "native/linux/x86_64/libEffekseerNativeForJava.so");
        NativeLibraryLoader.registerNativeLibrary("effekseer", Platform.Windows64, "native/windows/x86_64/EffekseerNativeForJava.dll");
        NativeLibraryLoader.loadNativeLibrary("effekseer", true);
        EffekseerBackendCore.InitializeAsOpenGL();
    }
    // TODO: Solve properly (Good enough for now to write it onto there and bring it that way into EffekseerControl.update)
    static boolean IS_SRGB;

    public static void registerLoader(AssetManager assetManager) {
        assetManager.registerLoader(EffekseerLoader.class, "efkefc");
    }

    public static void unregisterLoader(AssetManager assetManager) {
        assetManager.unregisterLoader(EffekseerLoader.class);
    }

    public static EffekseerProcessor addToViewPort(ViewPort viewPort, AssetManager assetManager, boolean sRGB) {
        return addToViewPort(viewPort, assetManager, sRGB, false);
    }

    public static EffekseerProcessor addToViewPort(ViewPort viewPort, AssetManager assetManager, boolean sRGB, boolean isOrthographic) {
        return addToViewPort(viewPort, assetManager, sRGB, isOrthographic, true);
    }

    public static EffekseerProcessor addToViewPort(ViewPort viewPort, AssetManager assetManager, boolean sRGB, boolean isOrthographic, boolean hasDepth) {
        IS_SRGB = sRGB;
        EffekseerProcessor processor = new EffekseerProcessor(assetManager, sRGB, isOrthographic, hasDepth);
        viewPort.addProcessor(processor);
        return processor;
    }
}
