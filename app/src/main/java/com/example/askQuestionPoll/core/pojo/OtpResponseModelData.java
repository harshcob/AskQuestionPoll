package com.example.askQuestionPoll.core.pojo;

public class OtpResponseModelData {
    public String token;
    public OtpResponseModelDataUser user = new OtpResponseModelDataUser();

    public OtpResponseModelData() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public OtpResponseModelDataUser getUser() {
        return user;
    }

    public void setUser(OtpResponseModelDataUser user) {
        this.user = user;
    }
}
