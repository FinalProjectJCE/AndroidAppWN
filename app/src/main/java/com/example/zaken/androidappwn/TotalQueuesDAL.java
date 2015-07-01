package com.example.zaken.androidappwn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Zaken on 02/04/2015.
 */
public class TotalQueuesDAL extends AsyncTask<String,Integer,Integer>
{
    private final Activity activity;
    private final Context context;
    private int branchId;
    private Exception exceptionToBeThrown;
    private TotalQueuesBL tqbl;
    private String DB_URL,USER,PASS;


    public TotalQueuesDAL(TotalQueuesBL tqbl,Activity activity,Context context,int branchId) {
        this.tqbl=tqbl;
        this.activity = activity;
        this.context=context;
        this.branchId=branchId;
        DB_URL = DatabaseConstants.DB_URL;
        PASS= DatabaseConstants.PASS;
        USER=DatabaseConstants.USER;
    }

    @Override
    protected Integer doInBackground(String... sqlQ) {
        int response = 0;
        try {
            boolean running = true;
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            Statement st2 = con.createStatement();
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
            exceptionToBeThrown = e;
            e.printStackTrace();
        }
        return response;
    }
    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (exceptionToBeThrown != null) {
            tqbl.connectionProblemAlert();
        }
    }
    protected void onProgressUpdate(Integer... progress) {
        tqbl.setTextViews(progress);

    }
}
