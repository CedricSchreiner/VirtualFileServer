package services.interfaces;

import models.interfaces.SharedDirectory;
import models.interfaces.User;

import java.util.List;

public interface SharedDirectoryService {
    List<SharedDirectory> getSharedDirectoryService(User iob_user);
}
