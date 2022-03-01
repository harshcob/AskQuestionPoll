package com.example.askQuestionPoll.core.pojo;

public class SettingResetPasswordResponseModel {
    public int code;
    public String message;
    public String cause;
    public EmptyModel data;

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
