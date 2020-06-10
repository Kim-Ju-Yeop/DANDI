package org.techtown.schooler.Model;

public class JoinedChannel {

    private String id;
    private String name;
    private String explain;
    private String create_user;
    private String color;
    private String school_id;
    private String isPublic;
    private String createAt;
    private String thumbnail;


    public String getThumbnail() {
        return thumbnail;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getCreate_user() {
        return create_user;
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getExplain() {
        return explain;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setCreate_user(String create_user) {
        this.create_user = create_user;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }
}
