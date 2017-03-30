package com.application.dissertation.photorun;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import static com.application.dissertation.photorun.R.menu.run_list_options;


/**
 * Created by Mizan on 03/03/2015.
 */
public class RunListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final int REQUEST_NEW_RUN = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);


        // Initialize loader to load the list of runs
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Intent i = new Intent(getActivity(), RunActivity.class);

        // Since we named the ID column of the run table _id, CursorAdapter has detected
        // it and passed the id to onListItemClick's id parameter. We can then pass that
        // id straight to RunActivity's extra
        i.putExtra(RunActivity.EXTRA_RUN_ID, id);

        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(run_list_options, menu);


    }

    public static RunListFragment newInstance(){

        RunListFragment frag = new RunListFragment();
        return frag;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_item_new_run:
                Intent i = new Intent(getActivity(), RunActivity.class);
                startActivityForResult(i, REQUEST_NEW_RUN);
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

        if (REQUEST_NEW_RUN == requestCode) {
            updateList();
        }
    }

    /**
     * Updates list by restarting loader
     */
    private void updateList() {

        // Restart loader to get any new Run available.
        getLoaderManager().restartLoader(0, null, this);
    }

    private static class RunListCursorLoader extends SQLiteCursorLoader {

        public RunListCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {

            // Query the list of runs
            return RunManager.get(getContext()).queryRuns();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Called when LoadManager needs you to create the loader

        return new RunListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        // Called on the main thread once data has been loaded in the background.

        RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), (RunDatabaseHelper.RunCursor) cursor);
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
        String shareBody = "Here is the share content body";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        //startActivity(Intent.createChooser(sharingIntent, "Share via"));
        Intent chooser = Intent.createChooser(sharingIntent, "Share Via");
        startActivity(chooser);




    }

    private static class RunCursorAdapter extends CursorAdapter {

        private RunDatabaseHelper.RunCursor mRunCursor;

        public RunCursorAdapter(Context context, RunDatabaseHelper.RunCursor cursor) {
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
            if (RunManager.get(context).isTrackingRun(run)) {
                view.setBackgroundColor(Color.RED);
            }
            // ELSE make it white. *This is important. Without this, items that were
            // set red because they WERE tracking will stay red because nothing is
            // setting them back to normal colour.
            else {
                view.setBackgroundColor(Color.WHITE);
            }

            // Set up the start date textview
            TextView startDateTextView = (TextView) view; // Cast view as TextView
            String cellText = context.getString(R.string.cell_text, run.getStartDate());
            startDateTextView.setText(cellText);
        }
    }
}



