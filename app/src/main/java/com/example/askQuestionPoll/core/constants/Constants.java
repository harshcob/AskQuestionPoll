package com.example.askQuestionPoll.core.constants;

public class Constants {

    public static final String API_SIGNUP = Extras.BASE_URL + "signup";
    public static final String API_LOGIN = Extras.BASE_URL + "loginForUser";
    public static final String API_SIGNUP_OTP_VERIFY = Extras.BASE_URL + "verifyUser";   //might send you new token due to after this user will be logged in
    public static final String API_SIGNUP_OTP_RESEND = Extras.BASE_URL + "resendOtpForUser";

    public static final String API_FORGOT_OTP_VERIFY = Extras.BASE_URL + "verifyOtpForUser";
    public static final String API_RESET_PASSWORD = Extras.BASE_URL + "generateNewPasswordForUser";
    public static final String API_FORGOT_PASSWORD = Extras.BASE_URL + "forgotPasswordForUser";

    public static final String API_VIEW_QUESTION = Extras.BASE_URL + "getAllQuestionByUser";
    public static final String API_REPORT_QUESTION = Extras.BASE_URL + "manageReportQuestion";
    public static final String API_ADD_QUESTION = Extras.BASE_URL + "addQuestion";
    public static final String API_ADD_ANSWER = Extras.BASE_URL + "addAnswer";
    public static final String API_VIEW_CATEGORIES = Extras.BASE_URL + "viewCategories";
    public static final String API_LOGOUT = Extras.BASE_URL + "logout";
    public static final String API_EDIT_PROFILE_USER = Extras.BASE_URL + "editProfileForUser";
    public static final String API_RESET_PASSWORD_USER = Extras.BASE_URL + "resetPasswordForUser";
    public static final String API_MY_QUESTIONS = Extras.BASE_URL + "viewQuestions";

    public static final String API_ANALYSIS_COUNTRY = Extras.BASE_URL + "viewQuestionAnalysisCountry";
    public static final String API_ANALYSIS_GENDER = Extras.BASE_URL + "viewQuestionAnalysisGender";
    public static final String API_ANALYSIS_NONE = Extras.BASE_URL + "viewQuestionAnalysisNone";

    public static final String MY_SHARED_PREFERENCE = "AskQuestionData";
}
