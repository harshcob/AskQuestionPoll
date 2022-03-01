package com.example.askQuestionPoll.core.pojo;

import com.google.gson.annotations.SerializedName;

public class LoginRequestModel {


    public String email_id;
    public String password;
    public LoginRequestModelDeviceInfo device_info;

    public LoginRequestModel() {
        device_info = new LoginRequestModelDeviceInfo();
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginRequestModelDeviceInfo getDevice_info() {
        return device_info;
    }

    public void setDevice_info(LoginRequestModelDeviceInfo device_info) {
        this.device_info = device_info;
    }

}

