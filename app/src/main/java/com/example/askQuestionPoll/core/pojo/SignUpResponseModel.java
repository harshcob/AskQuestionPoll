package com.example.askQuestionPoll.core.pojo;

public class SignUpResponseModel {
    public int code;
    public String message;
    public String cause;
    public SignUpResponseModelData data = new SignUpResponseModelData();

    public SignUpResponseModel() {
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

    public SignUpResponseModelData getData() {
        return data;
    }

    public void setData(SignUpResponseModelData data) {
        this.data = data;
    }


}
