package com.example.askQuestionPoll.core.pojo;

public class SignUpOtpVerifyResponseModel {
    public int code;
    public String message;
    public String cause;
    public OtpResponseModelData data = new OtpResponseModelData();

    public SignUpOtpVerifyResponseModel() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public OtpResponseModelData getData() {
        return data;
    }

    public void setData(OtpResponseModelData data) {
        this.data = data;
    }


}

