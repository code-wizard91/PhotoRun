package com.application.dissertation.photorun;

/**
 * Created by Mizan on 20/03/2015.
 */
import android.support.v4.app.Fragment;

public class ActivityMapRun extends SingleFragmentActivity {
    /** A key for passing a run ID as a long */
    public static final String EXTRA_RUN_ID = "RUN_ID";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (runId != -1) {
            return RunMainMapFragment.newInstance(runId);
        } else {
            return new RunMainMapFragment();
        }
    }

}