package com.example.zaken.androidappwn;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DBDAL extends AsyncTask<String, Void, String>
{

    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    String DB_URL = "jdbc:mysql://f37fa280-507d-4166-b70e-a427013f0c94.mysql.sequelizer.com:3306/dbf37fa280507d4166b70ea427013f0c94";
    String USER = "lewtprebbcrycgkb";
    String PASS = "S5zS2ExvQqZQrUK8dwSJvpv5dSvED4RwmijLrG55TEesXBTrAR3QDXPCGDPijZZU";
    protected String doInBackground(String... urls) {
        String response = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            String result = "\nDatabase connection success\n";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT CurrentQueue FROM Queue WHERE BusinessId = '111'");
            ResultSetMetaData rsmd = rs.getMetaData();

            while(rs.next()) {
                int currentQueue  = rs.getInt("CurrentQueue");
                System.out.print("\nCurrent Queue: " + currentQueue+"\n");
                GetQueue(currentQueue);
            }
            Log.d(result,"");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public void Connect()
    {
        DBDAL task = new DBDAL();
        task.execute();
    }

    public int GetQueue(int queue)
    {
        return queue;
    }
}