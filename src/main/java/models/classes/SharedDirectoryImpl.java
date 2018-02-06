package models.classes;

import models.interfaces.SharedDirectory;
import models.interfaces.User;

import java.util.List;

public class SharedDirectoryImpl implements SharedDirectory{
    private User owner;
    private List<User> members;
    private String directoryName;
    private int id;

    public SharedDirectoryImpl() {
    }

    public SharedDirectoryImpl(User owner, List<User> members, String directoryName) {
        this.owner = owner;
        this.members = members;
        this.directoryName = directoryName;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SharedDirectoryImpl{" +
                "owner=" + owner +
                ", members=" + members +
                ", directoryName='" + directoryName + '\'' +
                ", id=" + id +
                '}';
    }
}
