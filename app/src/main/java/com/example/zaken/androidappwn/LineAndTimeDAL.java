package com.example.zaken.androidappwn;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;

/**
 * Created by Zaken on 09/06/2015.
 */
public class LineAndTimeDAL extends AsyncTask<String,Object,Integer>
{
        private int branchId;
        private LineAndTimeBL latbl;
        String DB_URL =DatabaseConstants.DB_URL;
        String USER = DatabaseConstants.USER;
        String PASS = DatabaseConstants.PASS;

        public LineAndTimeDAL(LineAndTimeBL latbl,int branchId) {
            this.branchId=branchId;
            this.latbl=latbl;
        }

        @Override
        protected Integer doInBackground(String... sqlQ) {
            int response = 0;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                String result = "\nDatabase connection success\n";
                Statement st = con.createStatement();
                Statement st2 = con.createStatement();
                Statement st3 = con.createStatement();
                String query = "SELECT CurrentQueue,TotalQueue,AverageTime,NumberOfClerks FROM Queue WHERE BusinessId = '" + branchId + "'";
                String query3 ="UPDATE Queue SET TotalQueue = TotalQueue + 1 WHERE BusinessId = '" + branchId + "'";

                ResultSet rs = st.executeQuery(query);
                int rs3 = st3.executeUpdate(query3);
                while (rs.next()) {
                    int currentQueue = rs.getInt("CurrentQueue");
                    int totalQueue= rs.getInt("TotalQueue");
                    int numOfClerks = rs.getInt("NumberOfClerks");
                    Time time=rs.getTime("AverageTime");
                    int numOfPeopleForAverage = totalQueue-currentQueue;
                    publishProgress(currentQueue,totalQueue,time,numOfClerks);
                    response = currentQueue;
                }

                Log.d(result, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

    protected void onProgressUpdate(Object...progress)
    {
        latbl.setData(progress);
    }


}


