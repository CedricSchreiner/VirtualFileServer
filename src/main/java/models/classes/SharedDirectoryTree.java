package models.classes;

import java.io.File;
import java.io.IOException;

public class SharedDirectoryTree extends ObjectTree{
    private SharedDirectory gob_sharedDirectory;

    public SharedDirectoryTree(SharedDirectory iob_sharedDirectory, String iva_rootDirectory) throws IOException {
        super(iva_rootDirectory);
        this.gob_sharedDirectory = iob_sharedDirectory;

        try {
            File lob_userDirectory = new File(iva_rootDirectory);

            if (!lob_userDirectory.exists() || !lob_userDirectory.isDirectory()) {
                lob_userDirectory.mkdir();
            }

            super.addFilesToTree(lob_userDirectory);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SharedDirectory getSharedDirectory() {
        return this.gob_sharedDirectory;
    }

    public void setSharedDirectory(SharedDirectory iob_sharedDirectory) {
        this.gob_sharedDirectory = iob_sharedDirectory;
    }
}
