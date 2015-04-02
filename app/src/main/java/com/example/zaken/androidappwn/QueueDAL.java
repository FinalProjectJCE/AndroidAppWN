package com.example.zaken.androidappwn;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import android.view.View;
import android.app.Activity;

/**
 * Created by Zaken on 02/04/2015.
 */
public class QueueDAL extends AsyncTask<String,Integer,Integer>
{
    private final Activity activity;
    private final Context context;
    private int branchId;
    TextView currentQueueDisplay;
    String DB_URL = "jdbc:mysql://f37fa280-507d-4166-b70e-a427013f0c94.mysql.sequelizer.com:3306/dbf37fa280507d4166b70ea427013f0c94";
    String USER = "lewtprebbcrycgkb";
    String PASS = "S5zS2ExvQqZQrUK8dwSJvpv5dSvED4RwmijLrG55TEesXBTrAR3QDXPCGDPijZZU";

    public QueueDAL(Activity activity,Context context,int branchId) {
        this.activity = activity;
        this.context=context;
        this.branchId=branchId;
        currentQueueDisplay = (TextView) activity.findViewById(R.id.currenQueueDisplay);
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
            while(running) {
                String query = "SELECT CurrentQueue FROM Queue WHERE BusinessId = '" + branchId + "'";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    int currentQueue = rs.getInt("CurrentQueue");
                    response = currentQueue;
                    publishProgress(currentQueue);
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

    protected void onProgressUpdate(Integer... progress) {
        currentQueueDisplay.setText(Integer.toString(progress[0]));
    }
}

