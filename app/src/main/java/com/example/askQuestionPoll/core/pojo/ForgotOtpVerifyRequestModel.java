package com.example.askQuestionPoll.core.pojo;

public class ForgotOtpVerifyRequestModel {
    public String email_id;
    public int token;

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }
}
