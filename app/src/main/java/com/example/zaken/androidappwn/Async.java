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
    private int index;
    private String business;
    private String resultSetString,resultSetString2;

    public Async(int queryID, Activity activity,Context context,int index,String business){
        this.activity=activity;
        this.context=context;
        this.queryID=queryID;
        this.index=index;
        this.business=business;
        if (queryID == 1) {
            query = "SELECT * FROM Cities";
            query2 = "SELECT DISTINCT BusinessId FROM Queue";
            resultSetString="cityId";
            resultSetString2="cityName";
        }
        else if (queryID == 2) {
            query = "SELECT businessName,businessId FROM Businesses WHERE cityId = '" + index + "'";
            resultSetString="businessId";
            resultSetString2="businessName";
        }
        else if (queryID == 3) {
            query = "SELECT branchName, branchId FROM Branch WHERE businessId = '" + index + "'";
            resultSetString="branchId";
            resultSetString2="branchName";
        }
    }

 //        AsyncTask<Params, Progress, Result>:
//
//        Params: the type of doInBackground()'s and execute() var-args parameters.
//        Progress: the type of publishProgress()'s and onProgressUpdate()'s var-args parameters.
//        Result: the type of doInBackground()'s return value, onPostExecute()'s parameter, onCancelled()'s parameter, and get()'s return value.
    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL = "jdbc:mysql://f37fa280-507d-4166-b70e-a427013f0c94.mysql.sequelizer.com:3306/dbf37fa280507d4166b70ea427013f0c94";
    String USER = "lewtprebbcrycgkb";
    String PASS = "S5zS2ExvQqZQrUK8dwSJvpv5dSvED4RwmijLrG55TEesXBTrAR3QDXPCGDPijZZU";
    static ArrayList<String> valuesList;

    protected void onPreExecute() {
    }


//###########################################################################
    protected ArrayList doInBackground(String... sqlQ) {
        Hashtable<String,Integer> tempHash=new Hashtable<String,Integer>();

        valuesList = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            valuesList = new ArrayList<String>();
            if (queryID == 1) {
                while (rs.next()) {
                    MainActivity2.cityTable.put(rs.getString(resultSetString2), rs.getInt(resultSetString));
                    valuesList.add(rs.getString(resultSetString2));
                }
            }
            if (queryID == 2)
            {
                while (rs.next())
                {
                    MainActivity2.businessTable.put(rs.getString(resultSetString2), rs.getInt(resultSetString));
                    valuesList.add(rs.getString(resultSetString2));
                }
            }
            if (queryID == 3)
            {
                while (rs.next())
                {
                    MainActivity2.branchTable.put(rs.getString(resultSetString2), rs.getInt(resultSetString));
                    valuesList.add(rs.getString(resultSetString2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valuesList;
    }
//###########################################################################

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
        else if (queryID == 3) {
            branchTypeSpinner = (Spinner) activity.findViewById(R.id.branch_type_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, valuesList);
            branchTypeSpinner.setAdapter(adapter);
        }
    }
}
