package com.application.dissertation.photorun;

/**
 * Created by Mizan on 20/03/2015.
 */
import android.content.Context;

public class RunLoader extends DataLoader<Run> {
    private long mRunId;

    public RunLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Run loadInBackground() {
        return RunManager.get(getContext()).getRun(mRunId);
    }

}