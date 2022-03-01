package com.example.askQuestionPoll.core.pojo;

public class LoginResponseModelData {
    public String token;
    public LoginResponseModelDataUserDetails user_details = new LoginResponseModelDataUserDetails();
    ;

    public LoginResponseModelData() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginResponseModelDataUserDetails getUser_details() {
        return user_details;
    }

    public void setUser_details(LoginResponseModelDataUserDetails user_details) {
        this.user_details = user_details;
    }

}
