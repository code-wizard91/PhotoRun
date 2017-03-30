package com.application.dissertation.photorun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Mizan on 08/04/2015.
 */
public class BMR_Calculator extends Fragment {

    public static Person person = new Person();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.userinfo,container,false);

        final EditText name = (EditText)rootView.findViewById(R.id.nameUserInfo);
        final EditText weight = (EditText)rootView.findViewById(R.id.weightUserInfo);
        final EditText height = (EditText)rootView.findViewById(R.id.heightUserInfo);
        final EditText age = (EditText)rootView.findViewById(R.id.ageuserinfo);
        Button submit = (Button)rootView.findViewById(R.id.buttonSubmit);

        //Below we fetch information that was previously stored in the online database
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("UserDetails");
        query.getInBackground("MoTBH1pGFk", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    String mName = object.getString("Name").toString().trim();
                    double mWeight = object.getDouble("Weight");
                    double mHeight = object.getDouble("Height");
                    double mAge = object.getDouble("Age");

                    //set the fields as users previous entered information
                    name.setText(mName);
                    weight.setText(""+mWeight);
                    height.setText(""+mHeight);
                    age.setText(""+mAge);

                } else {
                    Toast.makeText(rootView.getContext(),"ERROR Could Not Retrieve Data From Server.Parse",Toast.LENGTH_LONG);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //retreive data from fields
                final String mName = name.getText().toString().trim();
                final String mWeight = weight.getText().toString().trim();
                final String mHeight = height.getText().toString().trim();
                final String mAge = age.getText().toString().trim();

                //enter data into person object
                person.setName(mName);
                person.setWeight(Double.parseDouble(mWeight));
                person.setHeight(Double.parseDouble(mHeight));
                person.setAge(Double.parseDouble(mAge));

                /*
                delete previous data on database here
                 */
                ParseQuery<ParseObject> query = ParseQuery.getQuery("UserDetails");
                // if the user updates their details the database will be updated
                query.getInBackground("MoTBH1pGFk", new GetCallback<ParseObject>() {
                    public void done(ParseObject Users, ParseException e) {
                        if (e == null) {
                            // Now let's update it with some new data. In this case, only cheatMode and score
                            // will get sent to the Parse Cloud. playerName hasn't changed.
                            Users.put("Name", person.getName());
                            Users.put("Weight", person.getWeight());
                            Users.put("Height", person.getHeight());
                            Users.put("Age", person.getAge());
                            Users.saveInBackground();
                        }
                    }
                });

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, Summary_Fragment.newInstance())
                        .commit();


            }
        });

        return rootView;
    }


    public static BMR_Calculator newInstance(){

        BMR_Calculator BM = new BMR_Calculator();
        return BM;

    }








}

