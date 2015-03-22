package com.example.zaken.androidappwn;

/**
 * Created by Zaken on 22/03/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;


public class Async extends AsyncTask<String,ArrayList,ArrayList> {
    private final Activity activity;
    private final Context context;
    private String query;
    private int queryID;
    private Spinner cityTypeSpinner;
    private Spinner businessTypeSpinner;
    private Spinner branchTypeSpinner;
    private String city;
    private String business;

    public Async(int queryID, Activity activity,Context context,String city,String business){
        this.activity=activity;
        this.context=context;
        this.queryID=queryID;
        this.city=city;
        this.business=business;
        if (queryID == 1)
            query = "SELECT DISTINCT City FROM Queue";
        else if (queryID == 2)
            query = "SELECT DISTINCT BusinessName FROM Queue WHERE City = '" + city + "'";

    }
    static ArrayList<String> valuesList;
    //        AsyncTask<Params, Progress, Result>:
//
//        Params: the type of doInBackground()'s and execute() var-args parameters.
//        Progress: the type of publishProgress()'s and onProgressUpdate()'s var-args parameters.
//        Result: the type of doInBackground()'s return value, onPostExecute()'s parameter, onCancelled()'s parameter, and get()'s return value.
    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL = "jdbc:mysql://f37fa280-507d-4166-b70e-a427013f0c94.mysql.sequelizer.com:3306/dbf37fa280507d4166b70ea427013f0c94";
    String USER = "lewtprebbcrycgkb";
    String PASS = "S5zS2ExvQqZQrUK8dwSJvpv5dSvED4RwmijLrG55TEesXBTrAR3QDXPCGDPijZZU";
    int SI = 0;


    protected ArrayList doInBackground(String... sqlQ) {
        int response = 0;
        valuesList = null;
        try {
            boolean running = true;
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

            String result = "\nDatabase connection success\n";
            Statement st = con.createStatement();
            //String query = sqlQ[0];
            ResultSet rs = st.executeQuery(query);

            ResultSetMetaData rsmd = rs.getMetaData();
            valuesList = new ArrayList<String>();
            while (rs.next()) {
                valuesList.add(rs.getString("City"));
                publishProgress(valuesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valuesList;
    }

    protected void onProgressUpdate(ArrayList... progress) {
        //currentQueueDisplay.setText(Integer.toString(progress[0]));
        //System.out.println("\n" + "On PrograssUpdate" + "\n");
        //new MainActivity().AnswerToArray(progress[0]);
//        for (int i = 0; i < progress[0].size(); i++) {
//            System.out.println("\n" + i + "\n");
//            System.out.println(progress[0].get(i));
//            //System.out.println(progress[0]);
//        }
    }
    protected void onPostExecute (ArrayList valuesList)
    {
        if (queryID == 1) {
            cityTypeSpinner = (Spinner) activity.findViewById(R.id.city_type_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, valuesList);
            cityTypeSpinner.setAdapter(adapter);
        }
        else if (queryID == 2) {
            businessTypeSpinner = (Spinner) activity.findViewById(R.id.business_type_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, valuesList);
            businessTypeSpinner.setAdapter(adapter);
        }
        System.out.println("NOWWWWWWWWWWWW " );
            super.onPostExecute(valuesList);
    }
}
