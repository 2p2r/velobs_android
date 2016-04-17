package org.deuxpiedsdeuxroues.velobs.picture;

import java.io.File;

public abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}