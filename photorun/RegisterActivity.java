package com.application.dissertation.photorun;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class RegisterActivity extends ActionBarActivity {

    protected EditText Username;
    protected EditText Useremail;
    protected EditText Userpassword;
    protected Button SignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeruser);

        //here we initialise protected variables
        Username =(EditText)findViewById(R.id.usernameRegisterEditText);
        Useremail=(EditText)findViewById(R.id.emailRegisterEditText);
        Userpassword=(EditText)findViewById(R.id.passwordRegisterEditText);
        SignUp=(Button)findViewById(R.id.buttonSignUp);

        //here we listen to button click
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = Username.getText().toString().trim();
                String email = Useremail.getText().toString().trim();
                String password = Userpassword.getText().toString().trim();

                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){

                            Toast.makeText(RegisterActivity.this,"Success!",Toast.LENGTH_LONG).show();
                            //take user to homepage
                            Intent returnToHome = new Intent(RegisterActivity.this,navdraw_Activity.class);
                            startActivity(returnToHome);


                        }else{

                            Toast.makeText(RegisterActivity.this,"Registry Unsuccessful",Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });



    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
