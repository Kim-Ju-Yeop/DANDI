package org.techtown.schooler.Model;

public class UpdateChannelEvents {

    private String content;
    private String title;
    private String start_date;
    private String end_date;

    public UpdateChannelEvents(String content, String title, String start_date, String end_date) {
        this.content = content;
        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
