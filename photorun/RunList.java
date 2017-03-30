package com.application.dissertation.photorun;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import static com.application.dissertation.photorun.R.menu.run_list_options;


/**
 * Created by Mizan on 03/03/2015.
 */
public class RunList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final int REQUEST_RUN = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);


        // get loader to load the list of runs from the database
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Intent runlistIntent = new Intent(getActivity(), RunListHelper.class);

        // Since we named the ID column of the run table the CursorAdapter detects
        // it and passes the id to onListItemClick's as a parameter. We then pass the same
        // id straight to RunListHelper

        runlistIntent.putExtra(RunListHelper.EXTRA_RUN_ID, id);

        startActivity(runlistIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCurrentList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(run_list_options, menu);


    }

    public static RunList newInstance(){

        RunList frag = new RunList();
        return frag;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_item_new_run:
                Intent i = new Intent(getActivity(), RunListHelper.class);
                startActivityForResult(i, REQUEST_RUN);
                return true;

            case R.id.share:
                shareIt();


                return true;





            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (REQUEST_RUN == requestCode) {
            updateCurrentList();
        }
    }

    /**
     * Updates list by restarting loader
     */
    private void updateCurrentList() {

        // Restart loader to get any new Run available.
        getLoaderManager().restartLoader(0, null, this);
    }

    private static class RunListDatabaseLoader extends SQLiteDatabaseLoader {

        public RunListDatabaseLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {

            // Query the list of runs
            return DatabaseManager.get(getContext()).queryRuns();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Called when LoadManager needs you to create the loader

        return new RunListDatabaseLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        // Called on the main thread once data has been loaded in the background.

        RunDatabaseAdapter adapter = new RunDatabaseAdapter(getActivity(), (RunListDatabaseHelper.RunCursor) cursor);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // Called in the event that data is no longer available.

        // Stop using the cursor
        setListAdapter(null);

    }

    private void shareIt() {
        //sharing implementation here
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey Check out this new app PhotoRun!";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "PhotoRun");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        Intent chooser = Intent.createChooser(sharingIntent, "Share Via");
        startActivity(chooser);
    }

    private static class RunDatabaseAdapter extends CursorAdapter {

        private RunListDatabaseHelper.RunCursor mRunCursor;

        public RunDatabaseAdapter(Context context, RunListDatabaseHelper.RunCursor cursor) {
            super(context, cursor, 0);
            mRunCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

            // Get a LayoutInflater
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Use layout inflater to get a row view
            return inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            // Get Run for current row.
            Run run = mRunCursor.getRun();

            // IF current Run is being tracked, make it red.
            if (DatabaseManager.get(context).isTrackingRun(run)) {
                view.setBackgroundColor(Color.RED);
            }
            //red represents a run in progress.
            //white represents a static run that is not being used.
            else {
                view.setBackgroundColor(Color.WHITE);
            }

            // Set up start date
            TextView startdate = (TextView) view; // instantiate textview
            String runInfoText = context.getString(R.string.run_Info, run.getStartDate());
            startdate.setText(runInfoText);
        }
    }
}



