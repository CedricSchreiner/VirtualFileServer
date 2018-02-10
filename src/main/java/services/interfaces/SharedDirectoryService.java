package services.interfaces;

import models.classes.SharedDirectory;
import models.classes.User;

import java.util.List;

public interface SharedDirectoryService {
    List<SharedDirectory> getSharedDirectoryService(User iob_user);
}
