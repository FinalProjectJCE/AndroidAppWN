package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zaken on 02/04/2015.
 */
public class QueueDAL extends AsyncTask<String,Object,Integer>
{
    private final Activity activity;
    private final Context context;
    private int branchId;
    private TextView currentQueueDisplay;
    private TextView averageTextView;
    private TextView waitingClients;
    private String DB_URL,USER,PASS;

    public QueueDAL(Activity activity,Context context,int branchId) {
        this.activity = activity;
        this.context=context;
        this.branchId=branchId;
        currentQueueDisplay = (TextView) activity.findViewById(R.id.currentQueueDisplayTV);
        averageTextView = (TextView) activity.findViewById(R.id.timeTextView);
        waitingClients = (TextView) activity.findViewById(R.id.waitingClientsDisplayTV);
        DB_URL = DatabaseConstants.DB_URL;
        PASS= DatabaseConstants.PASS;
        USER=DatabaseConstants.USER;
    }

    @Override
    protected Integer doInBackground(String... params) {
        int response = 0;
        try {
            boolean running = true;
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            String result = "\nDatabase connection success\n";
            Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            Statement st3 = con.createStatement();
            while(running) {
                String query = "SELECT CurrentQueue FROM Queue WHERE BusinessId = '" + branchId + "'";
                String query2 = "SELECT AverageTime FROM Queue WHERE BusinessId = '" + branchId + "'";
                String query3 = "SELECT TotalQueue FROM Queue WHERE BusinessId = '" + branchId + "'";
                ResultSet rs = st.executeQuery(query);
                ResultSet rs2 = st2.executeQuery(query2);
                ResultSet rs3 = st3.executeQuery(query3);
                while (rs.next()&& rs2.next()&& rs3.next()) {
                    int currentQueue = rs.getInt("CurrentQueue");
                    int totalQueue = rs3.getInt("TotalQueue");
                    Time t=rs2.getTime("AverageTime");
                    int numOfPeopleForAverage = totalQueue-currentQueue;
                    response = currentQueue;
                    if (numOfPeopleForAverage<1)
                        numOfPeopleForAverage=0;
                    publishProgress(currentQueue,t,numOfPeopleForAverage);

                }
                if(isCancelled())
                    running=false;
            }
            Log.d(result, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    protected void onProgressUpdate(Object...progress) {
//

        Time t = (Time) progress[1];

        currentQueueDisplay.setText(progress[0].toString());
        averageTextView.setText(setAverage(t.getHours(),t.getMinutes(),t.getSeconds(),(Integer)progress[2]));
        waitingClients.setText(progress[2].toString());
    }

    private String setAverage(int receivedHours, int receivedMinutes, int receivedSeconds,int queueNum) {
        Long subtract,secondsRR,hours,newHours,newMinutes,newSeconds;

        secondsRR = TimeUnit.HOURS.toSeconds(receivedHours)+
                TimeUnit.MINUTES.toSeconds(receivedMinutes)+
                receivedSeconds;
        secondsRR = secondsRR*queueNum;
        newHours=TimeUnit.SECONDS.toHours(secondsRR);
        secondsRR=secondsRR-(newHours*3600);
        newMinutes=TimeUnit.SECONDS.toMinutes(secondsRR);
        secondsRR=secondsRR-(newMinutes*60);
        newSeconds=secondsRR;
//        System.out.println(""+receivedHours+":"+receivedMinutes+":"+receivedSeconds);
//        System.out.println(""+queueNum);
//        System.out.println(""+newHours+":"+newMinutes+":"+newSeconds);
        return newHours+":"+newMinutes+":"+newSeconds;
    }
}

