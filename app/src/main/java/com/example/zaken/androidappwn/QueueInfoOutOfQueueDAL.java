package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;

/**
 * Created by Zaken on 02/04/2015.
 * This Class Is The Data Access Layer When The User Is Not In Queue.
 */
public class QueueInfoOutOfQueueDAL extends AsyncTask<String,Object,Integer>
{
    private final Activity activity;
    private final Context context;
    private int branchId;
    private String DB_URL,USER,PASS;
    private QueueInfoOutOfQueueBL qbl;
    private Exception exceptionToBeThrown;


    public QueueInfoOutOfQueueDAL(QueueInfoOutOfQueueBL qbl, Activity activity, Context context, int branchId) {
        this.qbl=qbl;
        this.activity = activity;
        this.context=context;
        this.branchId=branchId;
        DB_URL = DatabaseConstants.DB_URL;
        PASS= DatabaseConstants.PASS;
        USER=DatabaseConstants.USER;
    }

    // doInBackground Starts The Connection To The DB And Sends The Data In The Process
    @Override
    protected Integer doInBackground(String... params) {
        int response = 0;
        try {
            boolean running = true;
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            String result = "\nDatabase connection success\n";
            Statement st = con.createStatement();

            while(running) {
                String query = "SELECT CurrentQueue,AverageTime,TotalQueue,NumberOfClerks FROM Queue WHERE BusinessId = '" + branchId + "'";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    int currentQueue = rs.getInt("CurrentQueue");
                    int totalQueue = rs.getInt("TotalQueue");
                    int numOfClerks = rs.getInt("NumberOfClerks");
                    Time time=rs.getTime("AverageTime");
                    int numOfPeopleForAverage = totalQueue-currentQueue;
                    response = currentQueue;
                    if (numOfPeopleForAverage<1)
                        numOfPeopleForAverage=0;
                    publishProgress(currentQueue,time,numOfPeopleForAverage,numOfClerks,totalQueue);

                }
                if(isCancelled())
                    running=false;
            }
            Log.d(result, "");
        } catch (Exception e) {
            // If There Is A Connection Problem, Set This Exception.
            exceptionToBeThrown = e;
            e.printStackTrace();
        }
        return response;
    }

    // If There Is A Connection Problem, Activate The Handler Method.
    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (exceptionToBeThrown != null) {
            qbl.connectionProblemAlert();
        }
    }
    // Sends The Data.
    protected void onProgressUpdate(Object...progress)
    {
        qbl.setTextViews(progress);
    }
}

