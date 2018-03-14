package models.classes;

import java.io.File;

public class DownloadContent {
    private File gob_file;
    private int gva_version;

    public DownloadContent(File iob_file, int iva_version) {
        this.gob_file = iob_file;
        this.gva_version = iva_version;
    }

    public File getFile() {
        return this.gob_file;
    }

    public int getVersion() {
        return gva_version;
    }
}
