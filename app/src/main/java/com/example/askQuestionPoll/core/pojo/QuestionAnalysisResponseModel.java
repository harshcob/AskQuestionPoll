package com.example.askQuestionPoll.core.pojo;

public class QuestionAnalysisResponseModel {
    public int code;
    public String message;
    public String cause;
    public QuestionAnalysisResponseModelData data;

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

    public QuestionAnalysisResponseModelData getData() {
        return data;
    }

    public void setData(QuestionAnalysisResponseModelData data) {
        this.data = data;
    }
}
