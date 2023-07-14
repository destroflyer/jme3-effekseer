package com.destroflyer.jme3.effekseer.nativ;

import Effekseer.swig.EffekseerEffectCore;
import Effekseer.swig.EffekseerTextureType;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoadException;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class EffekseerLoader implements AssetLoader {

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        InputStream inputStream = assetInfo.openStream();
        return loadEffect(assetInfo.getManager(), assetInfo.getKey().getName(), inputStream);
    }

    public static EffekseerEffectCore loadEffect(AssetManager assetManager, String path) throws IOException {
        InputStream inputStream = EffekseerStreamUtils.openStream(assetManager, "", path);
        return loadEffect(assetManager, path, inputStream);
    }

    public static EffekseerEffectCore loadEffect(AssetManager assetManager, String path, InputStream inputStream) throws IOException {
        byte[] data = EffekseerStreamUtils.readAll(inputStream);
        return loadEffect(assetManager, path, data);
    }

    public static EffekseerEffectCore loadEffect(AssetManager assetManager, String path, byte[] data) throws IOException {
        byte[] bytes = data;

        String root = path.contains("/") ? path.substring(0, path.lastIndexOf("/")) : "";

        EffekseerEffectCore effectCore = new EffekseerEffectCore();
        if (!effectCore.Load(bytes, bytes.length, 1f)) {
            throw new AssetLoadException("Can't load effect "+path);
        }

        EffekseerTextureType[] textureTypes = new EffekseerTextureType[] {
            EffekseerTextureType.Color,
            EffekseerTextureType.Normal,
            EffekseerTextureType.Distortion
        };

        // Textures
        for (int t = 0; t < 3; t++) {
            for(int i = 0; i < effectCore.GetTextureCount(textureTypes[t]); i++) {
                String p = effectCore.GetTexturePath(i, textureTypes[t]);
                InputStream iss = EffekseerStreamUtils.openStream(assetManager,root,p);
                bytes = EffekseerStreamUtils.readAll(iss);
                if (!effectCore.LoadTexture(bytes, bytes.length, i, textureTypes[t])) {
                    throw new AssetLoadException("Can't load effect texture " + p);
                }
            }
        }

        // Models
        for (int i = 0; i < effectCore.GetModelCount(); i++) {
            String p = effectCore.GetModelPath(i);
            InputStream iss = EffekseerStreamUtils.openStream(assetManager, root, p);
            bytes = EffekseerStreamUtils.readAll(iss);
            if (!effectCore.LoadModel(bytes, bytes.length, i)) {
                throw new AssetLoadException("Can't effect load model " + p);
            }
        }

        // Materials
        for (int i = 0; i < effectCore.GetMaterialCount(); i++) {
            String p = effectCore.GetMaterialPath(i);
            InputStream iss = EffekseerStreamUtils.openStream(assetManager, root, p);
            bytes = EffekseerStreamUtils.readAll(iss);
            if (!effectCore.LoadMaterial(bytes, bytes.length, i) ) {
                throw new AssetLoadException("Can't load effect material "+p);
            }
        }

        // TODO: Sounds?

        return effectCore;
    }
}
