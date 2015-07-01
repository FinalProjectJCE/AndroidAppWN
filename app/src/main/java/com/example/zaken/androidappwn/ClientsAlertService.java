package com.example.zaken.androidappwn;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Zaken on 28/05/2015.
 */
public class ClientsAlertService extends Service {

    Thread myTread;
    boolean run;
    int branchId,userQueue,userChoice;
    String DB_URL =DatabaseConstants.DB_URL;
    String USER = DatabaseConstants.USER;
    String PASS = DatabaseConstants.PASS;
    private SharedPreferences sharedPrefQueue;

    public ClientsAlertService()
    {

    }

    @Override
    public void onCreate() {
        run = false;
        super.onCreate();
        Log.d("On Service Create", "");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", "");

        sharedPrefQueue = getSharedPreferences("MyPrefsFile",MODE_PRIVATE);
        userQueue = sharedPrefQueue.getInt("THE_LINE",0);
        branchId=sharedPrefQueue.getInt("BRANCH_ID",0);
        userChoice=sharedPrefQueue.getInt("USER_CLIENT_CHOICE",0)+1;

            //branchId = intent.getIntExtra("branchId", 0);
            //userQueue = intent.getIntExtra("userQueue", 0);
            if (!run) {
                run = true;
                Log.d("On Service Loop", "");


                myTread = new Thread() {
                    public void run() {
                        while (run) {
                            try {
                                Thread.sleep(10000);
                                try {
                                    Class.forName("com.mysql.jdbc.Driver");
                                    Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
                                    String result = "\nDatabase connection success\n";
                                    Statement st = con.createStatement();
                                    String query = "SELECT CurrentQueue FROM Queue WHERE BusinessId = '" + branchId + "'";
                                    ResultSet rs = st.executeQuery(query);
//                                ResultSet rs2 = st2.executeQuery(query2);

                                    while (rs.next()) {
                                        int currentQueue = rs.getInt("CurrentQueue");
                                        Log.d("Current Queue Is", "" + currentQueue);
                                        Log.d("User Queue Is", "" + userQueue);
                                        int g = userQueue - currentQueue;
                                        Log.d("uq-cq", "" + g);
                                        if (userQueue - currentQueue < userChoice) {
                                            onDestroy();

                                            goToAlarmPage();
                                        }

                                    }

                                    Log.d("Execute Query", "!!!!!");
                                    Log.d(result, "");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                };

                myTread.start();
            } else {
                branchId = intent.getIntExtra("branchId", 0);
            }
            return START_STICKY;

        }


    public void goToAlarmPage()
    {
        Intent alarm = new Intent();
        alarm.setClass(this,AlarmMainActivity.class);
        alarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alarm);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("Kill The Tread", "");
        run=false;
        super.onDestroy();
        stopSelf();

    }
}