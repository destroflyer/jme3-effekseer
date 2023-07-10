package com.destroflyer.jme3.effekseer.nativ;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class EffekseerStreamUtils {

    public static byte[] readAll(InputStream inputStream) throws IOException {
        byte[] chunk = new byte[1024 * 1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int read;
        while ((read = inputStream.read(chunk)) != -1) {
            bos.write(chunk, 0, read);
        }
        return bos.toByteArray();
    }

    private static String normalizePath(String... parts) {
        String path = "";

        for (String part : parts) {
            path += "/"+part.replace("\\", "/");
        }

        path = path.replace("/",File.separator);
        path = Paths.get(path).normalize().toString();
        path = path.replace("\\", "/");

        int sStr = 0;
        while (path.startsWith("../", sStr)) {
            sStr += 3;
        }
        path = path.substring(sStr);

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return path;
    }

    private static Collection<String> guessPossibleRelPaths(String root, String opath) {
        root = normalizePath(root);

        String path = opath;
        path = normalizePath(path);

        // System.out.println("Guesses for " + opath + " normalized " + path + " in root " + root);

        ArrayList<String> paths = new ArrayList<>();

        paths.add(path);
        paths.add(normalizePath(root, path));

        ArrayList<String> pathsNoRoot = new ArrayList<>();

        while (true) {
            int i = path.indexOf("/");
            if (i == -1) {
                break;
            }
            path = path.substring(i+1);
            if (path.isEmpty()) {
                break;
            }
            pathsNoRoot.add(path);
            paths.add(normalizePath(root, path));
        }

        paths.addAll(pathsNoRoot);

        return paths;
    }

    public static InputStream openStream(AssetManager assetManager, String rootPath, String path) {
        AssetInfo info = null;
        Collection<String> guessedPaths = guessPossibleRelPaths(rootPath,path);
        for (String p : guessedPaths) {
            try {
                // System.out.println("Try to locate assets " + Paths.get(path).getFileName() + " in " + p);
                info = assetManager.locateAsset(new AssetKey(p));
                if (info != null) {
                    // System.out.println("Found in " + p);
                    break;
                }
            } catch (AssetNotFoundException ex) {
                // System.out.println("Not found in " + p);
            }
        }
        if (info == null) {
            throw new AssetNotFoundException(path);
        }
        return info.openStream();
    }
}
