package com.application.dissertation.photorun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;


public class RunActivity extends SingleFragmentActivity {

    public static final String EXTRA_RUN_ID = "com.application.dissertation.photorun";

    @Override
    protected Fragment createFragment() {

        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);

        if (runId != -1){
            return RunFragment.newInstance(runId);
        }
        else{
            return new RunFragment();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Turn screen on when app runs
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }
}