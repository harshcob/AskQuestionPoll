package com.example.askQuestionPoll.core.pojo;

public class ForgotOtpVerifyResponseModel {
    public int code;
    public String message;
    public String cause;
    public ForgotOtpVerifyResponseModelData data;

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

    public ForgotOtpVerifyResponseModelData getData() {
        return data;
    }

    public void setData(ForgotOtpVerifyResponseModelData data) {
        this.data = data;
    }

}

