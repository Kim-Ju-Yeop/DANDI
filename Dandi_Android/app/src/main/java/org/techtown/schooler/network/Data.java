package org.techtown.schooler.network;

import com.google.gson.annotations.SerializedName;

import org.techtown.schooler.Model.Events;
import org.techtown.schooler.Model.ChannelInfo;
import org.techtown.schooler.Model.JoinedChannel;
import org.techtown.schooler.Model.SchoolInformations;
import org.techtown.schooler.Model.User;
import org.techtown.schooler.Model.UserInfo;

import java.util.List;

public class Data {
    // User
    private User user;

    // token
    @SerializedName("x-access-token")
    private String token;

    private String authCode;

    private Integer classCount;

    // IsOverlapped (true, false 를 확인하는 것이다.)
    // true 면 중복 false 면 중복 X
    private Boolean isOverlapped;

    private List<SchoolInformations> schoolInfo;

    private List<ChannelInfo> channels;//채널인포

    private ChannelInfo channelInfo;

    private List<JoinedChannel> joinedChannel;

    private List<Events> events;

    private UserInfo userInfo;

    private String channel_id;

    private ChannelInfo createdChannel;

    private List<UserInfo> awaitUsers;

    public List<UserInfo> getAwaitUsers() {
        return awaitUsers;
    }

    public void setAwaitUsers(List<UserInfo> awaitUsers) {
        this.awaitUsers = awaitUsers;
    }

    public ChannelInfo getCreatedChannel() {
        return createdChannel;
    }

    public void setCreatedChannel(ChannelInfo createdChannel) {
        this.createdChannel = createdChannel;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public void setChannelInfo(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    public List<Events> getEvents() {
        return events;
    }

    public void setEvents(List<Events> events) {
        this.events = events;
    }

    public List<ChannelInfo> getChannels() {
        return channels;
    }

    public List<JoinedChannel> getJoinedChannel() {
        return joinedChannel;
    }

    public void setJoinedChannel(List<JoinedChannel> joinedChannel) {
        this.joinedChannel = joinedChannel;
    }

    public void setChannels(List<ChannelInfo> channels) {
        this.channels = channels;
    }

    public Integer getClassCount() {
        return classCount;
    }

    public void setClassCount(Integer classCount) {
        this.classCount = classCount;
    }

    public List<SchoolInformations> getSchoolInfo() {
        return schoolInfo;
    }

    public void setSchoolInfo(List<SchoolInformations> schoolInfo) {
        this.schoolInfo = schoolInfo;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getOverlapped() {
        return isOverlapped;
    }

    public void setOverlapped(Boolean overlapped) {
        isOverlapped = overlapped;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}