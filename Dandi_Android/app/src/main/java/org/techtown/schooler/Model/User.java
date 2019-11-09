package org.techtown.schooler.Model;

public class User {
    private String user_name;
    private String user_id;
    private String user_pw;
    private String permission;
    private String user_email;
    private String user_phone;
    private String school;
    private String school_class;
    private String school_grade;
    private String profile_pic;
    private String pushNotify;
    private String isPublic;



//    public User(String user_id, String user_pw, String permission, String user_email, String user_phone, String school, String school_class, String school_grade, String prifle_pic, String pushNotify, String isPublic) {
//       this.user_id = user_id;
//       this.user_pw = user_pw;
//       this.permission = permission;
//       this.user_email = user_email;
//       this.user_phone = user_phone;
//       this.school = school;
//       this.school_class = school_class;
//       this.school_grade = school_grade;
//       this.profile_pic = prifle_pic;
//       this.pushNotify = pushNotify;
//       this.isPublic = isPublic;
//    }


    public String getUser_name() {
        return user_name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_pw() {
        return user_pw;
    }

    public String getPermission() {
        return permission;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public String getSchool() {
        return school;
    }

    public String getSchool_class() {
        return school_class;
    }

    public String getSchool_grade() {
        return school_grade;
    }

    public String getPrifle_pic() {
        return profile_pic;
    }

    public String getPushNotify() {
        return pushNotify;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_pw(String user_pw) {
        this.user_pw = user_pw;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setSchool_class(String school_class) {
        this.school_class = school_class;
    }

    public void setSchool_grade(String school_grade) {
        this.school_grade = school_grade;
    }

    public void setPrifle_pic(String prifle_pic) {
        this.profile_pic = prifle_pic;
    }

    public void setPushNotify(String pushNotify) {
        this.pushNotify = pushNotify;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }
}
