package models.interfaces;

import java.util.List;

public interface SharedDirectory {
    User getOwner();
    void setOwner(User owner);
    List<User> getMembers();
    void setMembers(List<User> members);
    String getDirectoryName();
    void setDirectoryName(String directoryName);
    int getId();
    void setId(int id);
}
