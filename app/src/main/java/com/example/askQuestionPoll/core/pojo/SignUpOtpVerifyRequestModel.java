package com.example.askQuestionPoll.core.pojo;

public class SignUpOtpVerifyRequestModel {
    public int user_reg_temp_id;
    public int token;
    public OtpRequestModelDeviceInfo device_info ;

    public SignUpOtpVerifyRequestModel() {
        device_info = new OtpRequestModelDeviceInfo();
    }

    public int getUser_reg_temp_id() {
        return user_reg_temp_id;
    }

    public void setUser_reg_temp_id(int user_reg_temp_id) {
        this.user_reg_temp_id = user_reg_temp_id;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public OtpRequestModelDeviceInfo getDevice_info() {
        return device_info;
    }

    public void setDevice_info(OtpRequestModelDeviceInfo device_info) {
        this.device_info = device_info;
    }

}
