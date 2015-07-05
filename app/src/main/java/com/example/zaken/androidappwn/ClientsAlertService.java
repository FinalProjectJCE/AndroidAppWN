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
 * This Is The Service That Checks How Many Clients Left Before The User.
 * This Is Activated After The User Choose To Get An Alert
 */
public class ClientsAlertService extends Service {

    Thread myTread;
    boolean run;
    private int branchId,userQueue,userChoice;
    private String DB_URL =DatabaseConstants.DB_URL;
    private String USER = DatabaseConstants.USER;
    private String PASS = DatabaseConstants.PASS;
    private SharedPreferences sharedPrefQueue;


    public ClientsAlertService()
    {

    }

    @Override
    public void onCreate() {
        run = false;
        super.onCreate();
    }

    /**
     * Starts The Tread And Check If The Number Of Waiting Clients Before The User,
     * Is Lower Or Equal To His Choose.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPrefQueue = getSharedPreferences("MyPrefsFile",MODE_PRIVATE);
        userQueue = sharedPrefQueue.getInt("THE_LINE",0);
        branchId=sharedPrefQueue.getInt("BRANCH_ID",0);
        userChoice=sharedPrefQueue.getInt("USER_CLIENT_CHOICE",0)+1;
            if (!run) {
                run = true;

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

                                    while (rs.next()) {
                                        int currentQueue = rs.getInt("CurrentQueue");
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


    /**
     * When The Number Of Waiting Clients Before The User Is Lower Or Equal To His Choose,
     * The Service Starts The Alarm And Kill Itself.
     */
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
        run=false;
        super.onDestroy();
        stopSelf();
    }
}
