package com.example.askQuestionPoll.core.pojo;

import java.util.ArrayList;

public class ViewQuestionResponseModelData {
    public ArrayList<ViewQuestionResponseModelResult> result[];

    public ArrayList<ViewQuestionResponseModelResult>[] getResult() {
        return result;
    }

    public void setResult(ArrayList<ViewQuestionResponseModelResult>[] result) {
        this.result = result;
    }
}
