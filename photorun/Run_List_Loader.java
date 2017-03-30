package com.application.dissertation.photorun;

/**
 * Created by Mizan on 20/03/2015.
 */
import android.content.Context;

public class Run_List_Loader extends RunDatabaseDataLoader<Run> {
    private long RId;

    public Run_List_Loader(Context context, long runId) {
        super(context);
        RId = runId;
    }

    @Override
    public Run loadInBackground() {
        return DatabaseManager.get(getContext()).getRun(RId);
    }

}