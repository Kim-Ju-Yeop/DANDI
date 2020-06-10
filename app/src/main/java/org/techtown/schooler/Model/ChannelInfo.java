package org.techtown.schooler.Model;

import java.util.List;

public class ChannelInfo {
    private String name;
    private String explain;
    private String create_user;
    private String thumbnail;
    private String isPublic;
    private String id;
    private String color;
    private int userStatus;
    private List<User> users;
    private User createUser;

    public ChannelInfo(String name, String explain, String create_user, String thumbnail, String isPublic, String id, String color, int userStatus, List<User> users, User createUser) {
        this.name = name;
        this.explain = explain;
        this.create_user = create_user;
        this.thumbnail = thumbnail;
        this.isPublic = isPublic;
        this.id = id;
        this.color = color;
        this.userStatus = userStatus;
        this.users = users;
        this.createUser = createUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getCreate_user() {
        return create_user;
    }

    public void setCreate_user(String create_user) {
        this.create_user = create_user;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }
}
