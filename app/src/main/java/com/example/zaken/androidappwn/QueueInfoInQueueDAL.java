package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Zaken on 02/04/2015.
 * This Class Is The Data Access Layer When The User Is In Queue.
 */
public class QueueInfoInQueueDAL extends AsyncTask<String,Integer,Integer>
{
    private final Activity activity;
    private final Context context;
    private int branchId;
    private Exception exceptionToBeThrown;
    private QueueInfoInQueueBL tqbl;
    private String DB_URL,USER,PASS;


    public QueueInfoInQueueDAL(QueueInfoInQueueBL tqbl, Activity activity, Context context, int branchId) {
        this.tqbl=tqbl;
        this.activity = activity;
        this.context=context;
        this.branchId=branchId;
        DB_URL = DatabaseConstants.DB_URL;
        PASS= DatabaseConstants.PASS;
        USER=DatabaseConstants.USER;
    }

    // doInBackground Starts The Connection To The DB And Sends The Data In The Process
    @Override
    protected Integer doInBackground(String... sqlQ) {
        int response = 0;
        try {
            boolean running = true;
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            while(running) {
                String query = "SELECT CurrentQueue,TotalQueue FROM Queue WHERE BusinessId = '" + branchId + "'";
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    int currentQueue = rs.getInt("CurrentQueue");
                    int totalQueue= rs.getInt("TotalQueue");
                    response = currentQueue;
                    publishProgress(currentQueue,totalQueue);
                }
                if(isCancelled())
                    running=false;
            }
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
            tqbl.connectionProblemAlert();
        }
    }

    // Sends The Data.
    protected void onProgressUpdate(Integer... progress) {
        tqbl.setTextViews(progress);

    }
}
