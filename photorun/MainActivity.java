package com.application.dissertation.photorun;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;


public class MainActivity extends FragmentActivity{




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Here we check if the user is already logged in.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            //if user is logged in do nothing


        } else {

            //if there is no user logged in do this
            Intent gotologin = new Intent(MainActivity.this,LogonActivity.class);

            startActivity(gotologin);


        }

        Intent taketorun = new Intent(MainActivity.this,RunListActivity.class);
        startActivity(taketorun);







    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

       try{

           int id = item.getItemId();

           //noinspection SimplifiableIfStatement
           switch(id){

               case R.id.signoutUser:

                   //log user out if the sign out button is clicked

                   ParseUser.logOut();
                   Intent gotologinscreen = new Intent(this,LogonActivity.class);
                   startActivity(gotologinscreen);

                   break;


           }

       }catch(Exception e){

           Toast.makeText(MainActivity.this,"Logged Out",Toast.LENGTH_LONG).show();

       }


        return super.onOptionsItemSelected(item);
    }



}
