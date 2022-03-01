package com.example.askQuestionPoll.core.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.askQuestionPoll.core.constants.Constants;
import com.example.askQuestionPoll.core.constants.Extras;


public class CustomSharedPreference {
    public static CustomSharedPreference myInstance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;



    public boolean isTokenExpire() {
        return sharedPreferences.getBoolean(Extras.tokenExpire,false);
    }

    public void setTokenExpire(boolean tokenExpire) {
        editor.putBoolean(Extras.tokenExpire,tokenExpire);
        saveData();
    }

    public int getUserId() {
        return sharedPreferences.getInt(Extras.userId,-1);
    }

    public void setUserId(int userId) {
        editor.putInt(Extras.userId,userId);
        saveData();
    }

    public int getRewards() {
        return sharedPreferences.getInt(Extras.rewards, -1);
    }

    public void setRewards(int rewards) {
        editor.putInt(Extras.rewards,rewards);
        saveData();
    }

    public float getRating() {
        return sharedPreferences.getFloat(Extras.rating,0);
    }

    public void setRating(float rating) {
        editor.putFloat(Extras.rating,rating);
        saveData();
    }

    public String getUserName() {
        return sharedPreferences.getString(Extras.userName,"");
    }

    public void setUserName(String userName) {
        editor.putString(Extras.userName,userName);
        saveData();
    }

    public String getPassword() {
        return sharedPreferences.getString(Extras.password,"");
    }

    public void setPassword(String password) {
        editor.putString(Extras.password,password);
        saveData();
    }

    public String getTempToken() {
        return sharedPreferences.getString(Extras.tempToken,"");
    }

    public void setTempToken(String tempToken) {
        editor.putString(Extras.tempToken,tempToken);
        saveData();
    }

    public void setUdid(String udid) {
        editor.putString(Extras.udid,udid);
        saveData();
    }

    public String getUdid(){
        return sharedPreferences.getString(Extras.udid,"");
    }

    public static synchronized CustomSharedPreference getInstance(Context context){
        if (myInstance==null)
            myInstance = new CustomSharedPreference(context);
        return myInstance;
    }

    public Boolean getLogin() {
        return sharedPreferences.getBoolean(Extras.isLogin,false);
    }

    public void setLogin(Boolean login) {
        editor.putBoolean(Extras.isLogin,login);
        saveData();
    }

    public String getTokenApplication() {
        return sharedPreferences.getString(Extras.applicationToken,"");
    }

    public void setTokenApplication(String tokenApplication) {
        editor.putString(Extras.applicationToken,tokenApplication);
        saveData();
    }

    public String getEmailID() {
        return sharedPreferences.getString(Extras.emailId,"");
    }

    public void setEmailID(String emailID) {
        editor.putString(Extras.emailId,emailID);
        saveData();
    }


    public int getTempUserId() {
        return sharedPreferences.getInt(Extras.tempUserId,-1);
    }

    public void setTempUserId(int tempUserId) {
        editor.putInt(Extras.tempUserId,tempUserId);
        saveData();
    }


    public void saveData(){
        editor.apply();
    }

    public CustomSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.MY_SHARED_PREFERENCE,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
}
