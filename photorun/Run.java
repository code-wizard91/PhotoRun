package com.application.dissertation.photorun;

import java.util.Date;

/**
 * Created by Mizan on 25/02/2015.
 */
public class Run {
    private Date start;
    private long RunId;

    public Run() {
        RunId = -1;
        start = new Date();
    }

    public long getRunId(){

        return RunId;
    }

    public void setRunId(long idIn){

        RunId = idIn;

    }

    public Date getStartDate() {

        return start;
    }

    public void setStartDate(Date startDate) {
        start = startDate;


    }

    public int getDurationSeconds(long endMillis) {
        return (int)((endMillis - start.getTime()) / 1000);


    }

    public static String formatDuration(int durationSeconds) {
        int seconds = durationSeconds % 60;
        int minutes = ((durationSeconds - seconds) / 60) % 60;
        int hours = (durationSeconds - (minutes * 60) - seconds) / 3600;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
