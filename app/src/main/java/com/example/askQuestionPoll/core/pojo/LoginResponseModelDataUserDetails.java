package com.example.askQuestionPoll.core.pojo;

public class LoginResponseModelDataUserDetails {
    public String social_uid;
    public int id;
    public String first_name;
    public String last_name;
    public String email_id;
    public String gender;
    public String country;
    public String profile_img_original;
    public String profile_img_compress;
    public String profile_img_thumbnail;

    public LoginResponseModelDataUserDetails() {
    }

    public String getSocial_uid() {
        return social_uid;
    }

    public void setSocial_uid(String social_uid) {
        this.social_uid = social_uid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProfile_img_original() {
        return profile_img_original;
    }

    public void setProfile_img_original(String profile_img_original) {
        this.profile_img_original = profile_img_original;
    }

    public String getProfile_img_compress() {
        return profile_img_compress;
    }

    public void setProfile_img_compress(String profile_img_compress) {
        this.profile_img_compress = profile_img_compress;
    }

    public String getProfile_img_thumbnail() {
        return profile_img_thumbnail;
    }

    public void setProfile_img_thumbnail(String profile_img_thumbnail) {
        this.profile_img_thumbnail = profile_img_thumbnail;
    }
}
