package org.deuxpiedsdeuxroues.velobs.picture;


import java.io.File;

import android.os.Environment;

import org.deuxpiedsdeuxroues.velobs.picture.AlbumStorageDirFactory;

public final class BaseAlbumDirFactory extends AlbumStorageDirFactory {

    private static final String CAMERA_DIR = "/dcim/";

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File (
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }
}
