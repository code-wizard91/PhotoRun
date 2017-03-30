package com.application.dissertation.photorun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Application;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LogonActivity extends ActionBarActivity {

    //variables used to login
    protected EditText username;
    protected EditText password;
    protected Button login;
    protected Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);

        Parse.initialize(this, "EILT11RQgIB1zUQd8dgW5OhREq0j1xhtiM3UwF7M",
                "SvCvLLZRHCk3uUGeGQq1gH0N4zXkDocRsUQVWut6");
        //initialising varialbles

        username = (EditText)findViewById(R.id.usernameLogin);
        password = (EditText)findViewById(R.id.PasswordLogin);
        login = (Button)findViewById(R.id.buttonLogin);
        signUp = (Button)findViewById(R.id.SignUpbuttonOnLoginScreen);

        //listen to login click
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //When the user clicks the login button we store the information in the variables below
               String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                // here we log the user in using the username and password
                //login the user using details
                ParseUser.logInInBackground(user,pass,new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if(e == null){
                            //if there is no error Success!
                            Toast.makeText(LogonActivity.this,"Welcome!",Toast.LENGTH_LONG).show();
                            //take user to homepage
                            Intent taketohome = new Intent(LogonActivity.this,navdraw_Activity.class);
                            startActivity(taketohome);

                        }else{

                            //if error occurs show error
                            AlertDialog.Builder builder = new AlertDialog.Builder((LogonActivity.this));
                            builder.setMessage(e.getMessage());
                            builder.setTitle("Error");
                            builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    }
                });



            }
        });


        //Listen for signup button click

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takeToSignUp = new Intent(LogonActivity.this,RegisterActivity.class);
                startActivity(takeToSignUp);




            }
        });







   }





}
