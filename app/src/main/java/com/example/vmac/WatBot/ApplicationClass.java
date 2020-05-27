package com.example.vmac.WatBot;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;


public class ApplicationClass extends Application {
    public static final String APPLICATION_ID = "APPLICATION_ID of serverless backend";
    public static final String API_KEY = "api key of serverless backend";
    public static final String SERVER_URL = "https://api.backendless.com";
    public static BackendlessUser user;
    public static String Location;


    @Override
    public void onCreate(){
        super.onCreate();
        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(),
                APPLICATION_ID,
                API_KEY );
    }

}
