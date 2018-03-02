package models.classes;

import java.io.File;
import java.io.IOException;

public class UserTree extends ObjectTree{
    private User gob_user;

    public UserTree(User iob_user, String iva_rootDirectory) throws IOException {
        super(iva_rootDirectory);
        this.gob_user = iob_user;

        try {
            File lob_userDirectory = new File(iva_rootDirectory);

            super.addFilesToTree(lob_userDirectory);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return gob_user;
    }

    public void setUser(User iob_user) {
        this.gob_user = iob_user;
    }

}
