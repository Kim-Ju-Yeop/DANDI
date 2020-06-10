package org.techtown.schooler.Model;

public class CreateChannelRequest {
    private String explain;
    private String name;
    private String isPublic;
    private String color;


    public CreateChannelRequest(String explain, String name, String isPublic, String color) {
        this.explain = explain;
        this.name = name;
        this.isPublic = isPublic;
        this.color = color;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getExplain() {
        return explain;
    }

    public String getName() {
        return name;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public String getColor() {
        return color;
    }
}
