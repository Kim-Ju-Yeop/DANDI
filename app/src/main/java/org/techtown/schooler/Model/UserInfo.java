package org.techtown.schooler.Model;

public class UserInfo {

    String user_id;
    String user_name;
    String user_email;
    String user_phone;
    School school;
    String school_grade;
    String school_class;
    String profile_pic;
    String isPublic;

    public UserInfo(String user_id, String user_name, String user_email, String user_phone, School school, String school_grade, String school_class, String profile_pic, String isPublic) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_phone = user_phone;
        this.school = school;
        this.school_grade = school_grade;
        this.school_class = school_class;
        this.profile_pic = profile_pic;
        this.isPublic = isPublic;
    }

    public UserInfo(){

    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public School getSchool() {
        return school;
    }

    public String getSchool_class() {
        return school_class;
    }

    public String getSchool_grade() {
        return school_grade;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public void setSchool_class(String school_class) {
        this.school_class = school_class;
    }

    public void setSchool_grade(String school_grade) {
        this.school_grade = school_grade;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }
}
