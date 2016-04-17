package org.deuxpiedsdeuxroues.velobs.picture;

import java.io.File;

import android.os.Environment;

import org.deuxpiedsdeuxroues.velobs.picture.AlbumStorageDirFactory;

public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }
}
