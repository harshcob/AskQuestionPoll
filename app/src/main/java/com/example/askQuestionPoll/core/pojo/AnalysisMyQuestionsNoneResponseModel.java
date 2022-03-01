package com.example.askQuestionPoll.core.pojo;

public class AnalysisMyQuestionsNoneResponseModel {
    public int code;
    public String message;
    public String cause;
    public AnalysisMyQuestionsNoneResponseModelData data;

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

    public AnalysisMyQuestionsNoneResponseModelData getData() {
        return data;
    }

    public void setData(AnalysisMyQuestionsNoneResponseModelData data) {
        this.data = data;
    }
}

