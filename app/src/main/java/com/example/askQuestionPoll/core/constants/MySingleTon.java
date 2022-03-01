package com.example.askQuestionPoll.core.constants;

import android.content.Context;

import com.optimumbrew.library.core.volley.GsonRequest;
import com.optimumbrew.library.core.volley.MyVolley;
import com.optimumbrew.library.core.volley.PhotoMultipartRequest;

public class MySingleTon {
    private static MySingleTon mySingleTon;
    private MyVolley requestQueue;
    private static Context myContext;

    private MySingleTon(Context context) {
        this.myContext = context;
        this.requestQueue = MyVolley.getInstance(myContext);

    }
    public MyVolley getRequestQueue(){
        if (requestQueue==null){
            requestQueue = MyVolley.getInstance(myContext);
        }
        return requestQueue;
    }
    public static synchronized MySingleTon getInstance(Context context){
        if (mySingleTon==null){
            mySingleTon=new MySingleTon(context);
        }
        return mySingleTon;
    }
    public<T> void addGsonRequest(GsonRequest<T> request){
        requestQueue.addToRequestQueue(request);
    }
    public<T> void addPhotoMultiPartRequest(PhotoMultipartRequest<T> request){
        requestQueue.addToRequestQueue(request);
    }
}

