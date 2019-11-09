package org.techtown.schooler.Model;

public class ChannelEditRequest {
    private String isPublic;
    private String color;
    private String explain;


    public ChannelEditRequest(String isPublic, String color, String explain) {
        this.isPublic = isPublic;
        this.color = color;
        this.explain = explain;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public String getColor() {
        return color;
    }

    public String getExplain() {
        return explain;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
}

