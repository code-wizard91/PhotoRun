package com.application.dissertation.photorun;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Mizan on 18/03/2015.
 */
public abstract class RunDatabaseDataLoader<D> extends AsyncTaskLoader<D> {
    private D mData;

    public RunDatabaseDataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(D data) {
        mData = data;
        if (isStarted())
            super.deliverResult(data);
    }

}