package com.example.askQuestionPoll.core.pojo;

public class AnalysisMyQuestionGenderResponseModel {
    public int code;
    public String message;
    public String cause;
    public AnalysisMyQuestionGenderResponseModelData data;


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

    public AnalysisMyQuestionGenderResponseModelData getData() {
        return data;
    }

    public void setData(AnalysisMyQuestionGenderResponseModelData data) {
        this.data = data;
    }
}
