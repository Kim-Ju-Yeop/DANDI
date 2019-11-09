package org.techtown.schooler.Model;

import java.util.List;
import java.util.Set;

public class Events {

    Author author;
    Channel channel;
    String title;
    String start_date;
    String end_date;
    String content;
    String id;

    public Events(Author author, Channel channel, String title, String start_date, String end_date, String content, String id) {

        this.author = author;
        this.channel = channel;
        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.content = content;
        this.id = id;
    }

    public Events() {

    }

    public void setAuthor(Author author) {
        this.author = author;
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

    public Author getAuthor() {
        return author;
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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}