package com.example.zaken.androidappwn;

/**
 * Created by Zaken on 22/03/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;


public class Async extends AsyncTask<String,ArrayList,ArrayList> {
    private final Activity activity;
    private final Context context;
    private String query,query2;
    private int queryID;
    private Spinner cityTypeSpinner;
    private Spinner businessTypeSpinner;
    private Spinner branchTypeSpinner;
    private String city;
    private String business;
    private String resultSetString,resultSetString2;

    public Async(int queryID, Activity activity,Context context,String city,String business){
        this.activity=activity;
        this.context=context;
        this.queryID=queryID;
        this.city=city;
        this.business=business;
        if (queryID == 1) {
            query = "SELECT DISTINCT City FROM Queue";
            query2 = "SELECT DISTINCT BusinessId FROM Queue";
            resultSetString="City";
            resultSetString2="BusinessId";
        }
        else if (queryID == 2) {
            query = "SELECT DISTINCT BusinessName FROM Queue WHERE City = '" + city + "'";
            resultSetString="BusinessName";
        }

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

    protected void onPreExecute() {
        AsyncBL.tryMe="onPreExecute";
        System.out.println("\nonPreExecute : "+AsyncBL.tryMe+"\n");
    }
    protected ArrayList doInBackground(String... sqlQ) {
        Hashtable<String,Integer> hashTable=new Hashtable<String,Integer>();
        int response = 0;
        valuesList = null;
        try {
            boolean running = true;
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

            String result = "\nDatabase connection success\n";
            Statement st = con.createStatement();
            //Statement st2 = con.createStatement();
            //String query = sqlQ[0];
            System.out.println("\nDatabase connection success\n");
            ResultSet rs = st.executeQuery(query);
            //ResultSet rs2 = st2.executeQuery(query2);
            System.out.println("executeQuery with :" + query);
            ResultSetMetaData rsmd = rs.getMetaData();
            valuesList = new ArrayList<String>();
            System.out.println("HERE1");
            while (rs.next()) {
                valuesList.add(rs.getString(resultSetString));
                System.out.println("ValueList Item : " +rs.getString(resultSetString) );
                publishProgress(valuesList);
            }
            System.out.println("HERE2");
        } catch (Exception e) {
            e.printStackTrace();
        }
        AsyncBL.tryMe="doInBackground";
        System.out.println("\ndoInBackground : "+AsyncBL.tryMe+"\n");
        return valuesList;
    }

    protected void onProgressUpdate(ArrayList... progress) {
        AsyncBL.tryMe="onProgressUpdate";
        System.out.println("\nAfter execute "+AsyncBL.tryMe+"\n");
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
        System.out.println("Query id is: "+ queryID );
        for(int i=0;i<valuesList.size();i++) {
            System.out.println("\n"+i+"\n");
            System.out.println(valuesList.get(i));
        }

    }
}
