package com.application.dissertation.photorun;

/**
 * Created by Mizan on 26/01/2015.
 */


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;

import com.parse.ParseUser;

public class SplashScreen extends Activity {


    private static int splash_Delay = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash_screen);



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent myintent = new Intent(SplashScreen.this, LogonActivity.class);
                    startActivity(myintent);
                    finish();
                }
            },splash_Delay);





    }
}
