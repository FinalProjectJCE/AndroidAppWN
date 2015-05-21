package com.example.zaken.androidappwn;

/**
 * Created by Zaken on 22/03/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class Async extends AsyncTask<String,ArrayList,ArrayList> {
    private final Activity activity;
    private final Context context;
    private String query;
    private String idForDB,cityForDB,businessForDB,branchForDB,DB_URL,USER,PASS ,latitudeForDB,longitudeForDB,distanceForDB ;

    public Async(Activity activity,Context context){
        this.activity=activity;
        this.context=context;

        query = "SELECT "+DatabaseConstants.MAIN_ID+","+DatabaseConstants.CITIES+","+DatabaseConstants.BUSINESS+","+DatabaseConstants.BRANCH+","+DatabaseConstants.LATITUDE+","+DatabaseConstants.LONGITUDE+","+DatabaseConstants.DISTANCE+" FROM Queue";
        idForDB=DatabaseConstants.MAIN_ID;
        cityForDB=DatabaseConstants.CITIES;
        businessForDB=DatabaseConstants.BUSINESS;
        branchForDB = DatabaseConstants.BRANCH;
        DB_URL = DatabaseConstants.DB_URL;
        PASS= DatabaseConstants.PASS;
        USER=DatabaseConstants.USER;
        longitudeForDB = DatabaseConstants.LONGITUDE;
        latitudeForDB = DatabaseConstants.LATITUDE;
        distanceForDB = DatabaseConstants.DISTANCE;
    }

    protected ArrayList doInBackground(String... sqlQ) {
        ArrayList<String> valuesList;
        valuesList = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            valuesList = new ArrayList<String>();

            DB data  = new DB(context);
                while (rs.next())
                {
                    data.insertToDB(rs.getString(cityForDB), rs.getString(businessForDB), rs.getInt(idForDB), rs.getString(branchForDB),rs.getDouble(latitudeForDB),rs.getDouble(longitudeForDB),rs.getInt(distanceForDB));
                    Log.d("idForDB",rs.getInt(idForDB)+"");
                    Log.d("cityForDB",rs.getString(cityForDB));
                    Log.d("businessForDB",rs.getString(businessForDB));
                    Log.d("branchForDB",rs.getString(branchForDB));
                    Log.d("Longitude",rs.getDouble(longitudeForDB)+"");
                    Log.d("Latitude",rs.getDouble(latitudeForDB)+"");
                    Log.d("Distance",rs.getInt(distanceForDB)+"");
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return valuesList;
    }
}
