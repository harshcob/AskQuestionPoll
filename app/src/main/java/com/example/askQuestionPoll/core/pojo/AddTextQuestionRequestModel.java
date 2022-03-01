package com.example.askQuestionPoll.core.pojo;

public class AddTextQuestionRequestModel {
    public AddTextQuestionRequestModelData request_data;

    public AddTextQuestionRequestModel() {
        request_data = new AddTextQuestionRequestModelData();
    }

    public AddTextQuestionRequestModelData getRequest_data() {
        return request_data;
    }

    public void setRequest_data(AddTextQuestionRequestModelData request_data) {
        this.request_data = request_data;
    }
}

