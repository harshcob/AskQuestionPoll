package com.example.askQuestionPoll.core.pojo;

public class SignUpOtpResendResponseModel {
    public int code;
    public String message;
    public String cause;
    public EmptyModel data = new EmptyModel();

    public SignUpOtpResendResponseModel() {
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

    public EmptyModel getData() {
        return data;
    }

    public void setData(EmptyModel data) {
        this.data = data;
    }


}
