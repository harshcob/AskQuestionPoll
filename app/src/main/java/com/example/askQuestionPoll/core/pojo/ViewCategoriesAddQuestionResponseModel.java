package com.example.askQuestionPoll.core.pojo;

import java.util.ArrayList;

public class ViewCategoriesAddQuestionResponseModel {
    public int code;
    public String message;
    public String cause;
    public ViewCategoriesAddQuestionModelData data;

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

    public ViewCategoriesAddQuestionModelData getData() {
        return data;
    }

    public void setData(ViewCategoriesAddQuestionModelData data) {
        this.data = data;
    }


}

