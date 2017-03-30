package com.application.dissertation.photorun;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.w3c.dom.Text;


public class Summary_Fragment extends android.support.v4.app.Fragment {




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Person person = BMR_Calculator.person;
        View rootView = inflater.inflate(R.layout.summary_run, container, false);
        TextView maintain = (TextView) rootView.findViewById(R.id.textMaintain);
        TextView surplus = (TextView) rootView.findViewById(R.id.textGain);
        TextView defecit = (TextView) rootView.findViewById(R.id.textloseweight);

        //Below are the calculations that are below the table
        double weight = person.getWeight();
        double height = person.getHeight();
        double age = person.getAge();

            double maintainance = weight * 13.75 + 66.47 + 5.0 * height - 6.75 * age;
            double gainweightformula = weight * 13.75 + 66.47 + 5.0 * height - 6.75 * age + 300;
            double loseweightformula = weight * 13.75 + 66.47 + 5.0 * height - 6.75 * age - 600;

            //parse double into an int in order to remove decimal points.
            int num = Integer.parseInt(String.valueOf(maintainance).split("\\.")[0]);

            maintain.setText("Mainainance Calories: " + maintainance + " Calories");
            surplus.setText("Calories to Gain Weight: " + gainweightformula + " Calories");
            defecit.setText("Calories to Lose Weight: " + loseweightformula + " Calories");

            ///table textviews and calculations are below
            TextView carbsday1 = (TextView) rootView.findViewById(R.id.carbday1);
            TextView carbsday2 = (TextView) rootView.findViewById(R.id.carbday2);
            TextView carbsday3 = (TextView) rootView.findViewById(R.id.carbday3);
            TextView carbsday4 = (TextView) rootView.findViewById(R.id.carbday4);
            TextView carbsday5 = (TextView) rootView.findViewById(R.id.carbday5);

            TextView proday1 = (TextView) rootView.findViewById(R.id.proday1);
            TextView proday2 = (TextView) rootView.findViewById(R.id.proday2);
            TextView proday3 = (TextView) rootView.findViewById(R.id.proday3);
            TextView proday4 = (TextView) rootView.findViewById(R.id.proday4);
            TextView proday5 = (TextView) rootView.findViewById(R.id.proday5);

            TextView fats1 = (TextView) rootView.findViewById(R.id.fats1);
            TextView fats2 = (TextView) rootView.findViewById(R.id.fats2);
            TextView fats3 = (TextView) rootView.findViewById(R.id.fats3);
            TextView fats4 = (TextView) rootView.findViewById(R.id.fats4);
            TextView fats5 = (TextView) rootView.findViewById(R.id.fats5);

            //calculating calorie requirements on a 40/40/20 macronutrient split
            double calorieday1 = num / 100 * 40 / 4;
            double calorieday2 = num / 100 * 40 / 4 - 50;
            double calorieday3 = num / 100 * 40 / 4 - 100;
            double calorieday4 = num / 100 * 40 / 4 - 90;
            double calorieday5 = num / 100 * 40 / 4 + 250;
            double protiensaday1 = num / 100 * 40 / 5;
            double fatsperday1 = num / 100 * 20 / 9;


            //here are the percentages used in the pie chart
            final double prosaday1 = num / 100 * 40;
            final double carsaday1 = num / 100 * 40;
            final double fatsaday1 = num / 100 * 20;


            //showing the user how many carbs they need
            carbsday1.setText("" + calorieday1);
            carbsday2.setText("" + calorieday2);
            carbsday3.setText("" + calorieday3);
            carbsday4.setText("" + calorieday4);
            carbsday5.setText("" + calorieday5);

            //showing user how much protien is needed
            proday1.setText("" + protiensaday1);
            proday2.setText("" + protiensaday1);
            proday3.setText("" + protiensaday1);
            proday4.setText("" + protiensaday1);
            proday5.setText("" + protiensaday1);

            fats1.setText("" + fatsperday1);
            fats2.setText("" + fatsperday1);
            fats3.setText("" + fatsperday1);
            fats4.setText("" + fatsperday1);
            fats5.setText("" + fatsperday1);



            Button btnchart = (Button) rootView.findViewById(R.id.btn);
            btnchart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenChart(prosaday1, carsaday1, fatsaday1);
                }
            });









        return rootView;


    }


    public static Summary_Fragment newInstance(){

        Summary_Fragment sf = new Summary_Fragment();
        return sf;

    }

    private void OpenChart(double calorieday1,double protiensaday1,double fatsaday1) {

        DefaultRenderer dR = new DefaultRenderer();
        dR.setChartTitle("Your Macro Nutrients");
        dR.setStartAngle(90);
        dR.setChartTitleTextSize(55);
        dR.setApplyBackgroundColor(true);
        dR.setBackgroundColor(Color.BLACK);
        dR.setLabelsTextSize(40);
        dR.setLegendTextSize(40);
        dR.setMargins(new int[]{20,30,15,0});
        dR.setZoomButtonsVisible(true);

        String[] subjects = new String[]{

                "Carbohydrates","Protiens","Fats"

        };

        double[] distributions = new double[]{

              calorieday1,protiensaday1,fatsaday1

        };

        int[] colors = new int[]{

                Color.BLUE,Color.YELLOW,Color.RED

        };

        CategorySeries category = new CategorySeries("Statistics");

        for (int i = 0; i < distributions.length;i++){

            category.add(subjects[i],distributions[i]);
        }



        for (int i = 0; i < distributions.length;i++){

            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();


            renderer.setColor(colors[i]);
            renderer.setDisplayChartValues(true);
            dR.addSeriesRenderer(renderer);


        }


        Intent in = ChartFactory.getPieChartIntent(getActivity().getBaseContext(),category,dR,"Pie Chart Demo");
        startActivity(in);




    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);




    }
}
